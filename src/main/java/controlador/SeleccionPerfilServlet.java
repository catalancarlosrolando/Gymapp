/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Cuenta;
import entidad.Perfil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import sesiones.PerfilFacade;
import sesiones.SesionActivaFacade;

/**
 * Pantalla intermedia estilo Netflix
 * @author carlos
 * 
 * /**
 * SeleccionPerfilServlet - Pantalla intermedia estilo Netflix.
 *
 * Flujo:
 *   GET /SeleccionPerfil
 *     - Lista los perfiles de la cuenta logueada
 *     - Marca cuáles están activos (sesión en BD)
 *     - Forward a seleccionPerfil.jsp con cards
 *
 *   POST /SeleccionPerfil?accion=elegir&idPerfil=X
 *     - Recupera el perfil X
 *     - Valida que pertenezca a la cuenta logueada
 *     - Llama a sesionF.intentarLogin(perfil) -> verifica cupo @Lock(WRITE)
 *     - Si OK: guarda perfilActivo en HttpSession, redirect a /Home
 *     - Si NO: forward a seleccionPerfil.jsp con error
 *
 * Aquí es donde se aplica el @Lock(WRITE) del SesionActivaFacade.
 */
@WebServlet(
    name = "SeleccionPerfilServlet",
    loadOnStartup = 2,
    urlPatterns = { "/SeleccionPerfil" }
)
public class SeleccionPerfilServlet extends HttpServlet {

    @EJB
    private PerfilFacade perfilF;

    @EJB
    private SesionActivaFacade sesionF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);

        // Verificar que hay cuenta logueada
        Cuenta cuenta = (session != null)
                      ? (Cuenta) session.getAttribute("cuentaLogueada")
                      : null;
        if (cuenta == null) {
            response.sendRedirect("Login");
            return;
        }

        String accion = request.getParameter("accion");

        if ("elegir".equals(accion)) {
            // POST: el usuario eligió un perfil
            String idParam = request.getParameter("idPerfil");
            if (idParam == null) {
                response.sendRedirect("SeleccionPerfil");
                return;
            }
            Integer idPerfil;
            try {
                idPerfil = Integer.parseInt(idParam);
            } catch (NumberFormatException ex) {
                response.sendError(400, "idPerfil inválido");
                return;
            }

            Perfil perfil = perfilF.findConCuenta(idPerfil);
            if (perfil == null
                    || perfil.getCuenta() == null
                    || !perfil.getCuenta().getIdcuenta().equals(cuenta.getIdcuenta())) {
                // Error de validación: mostrar modal genérico
                request.setAttribute("motivoRechazo", "INVALIDO");
                request.setAttribute("mensajeRechazo",
                        "Perfil inválido o no pertenece a tu cuenta.");
                listarYForward(request, response, cuenta);
                return;
            }

            // >>> AQUÍ SE APLICA @Lock(WRITE) <<<
            sesiones.SesionActivaFacade.ResultadoLogin res =
                    sesionF.intentarLogin(perfil);

            switch (res) {
                case ACEPTADO:
                    // OK: guardar perfilActivo en sesión HTTP y redirect (PRG)
                    session.setAttribute("perfilActivo", perfil);
                    response.sendRedirect("Home");
                    return;

                case PERFIL_YA_ACTIVO:
                    // El perfil ya tiene una sesión activa en OTRA ventana.
                    // Pasar al JSP datos estructurados para mostrar en un MODAL
                    // con la opción de "Cerrar sesión remota" desde ahí.
                    request.setAttribute("motivoRechazo", "PERFIL_YA_ACTIVO");
                    request.setAttribute("perfilBloqueadoId", perfil.getIdperfil());
                    request.setAttribute("perfilBloqueadoNombre", perfil.getNombre());
                    request.setAttribute("mensajeRechazo",
                            "El perfil '" + perfil.getNombre() + "' ya tiene una " +
                            "sesión activa en otra ventana o dispositivo.");
                    request.setAttribute("accionSugerida", "cerrarRemota");
                    request.setAttribute("textoAccionSugerida",
                            "Cerrar sesión remota de " + perfil.getNombre());
                    listarYForward(request, response, cuenta);
                    return;

                case PLAN_LLENO:
                    // El plan alcanzó su cupo de sesiones simultáneas.
                    // Pasar al JSP datos para mostrar en MODAL con info de cupos
                    // y (si hay) la lista de perfiles activos que se pueden cerrar.
                    request.setAttribute("motivoRechazo", "PLAN_LLENO");
                    request.setAttribute("mensajeRechazo",
                            "Tu plan " + cuenta.getPlan().getNombre() + " ya tiene " +
                            "el máximo de " + cuenta.getPlan().getCupos() + " perfiles " +
                            "activos simultáneamente.");
                    request.setAttribute("cuposPlan", cuenta.getPlan().getCupos());
                    request.setAttribute("planNombre", cuenta.getPlan().getNombre());
                    // Lista de perfiles activos que se podrían cerrar remotamente
                    List<Integer> activosIds = sesionF.perfilesActivosDeCuenta(cuenta.getIdcuenta());
                    List<Perfil> perfilesActivos = new java.util.ArrayList<>();
                    for (Integer id : activosIds) {
                        Perfil p = perfilF.findConCuenta(id);
                        if (p != null
                                && p.getCuenta() != null
                                && p.getCuenta().getIdcuenta().equals(cuenta.getIdcuenta())) {
                            perfilesActivos.add(p);
                        }
                    }
                    request.setAttribute("perfilesActivosParaCerrar", perfilesActivos);
                    listarYForward(request, response, cuenta);
                    return;

                default:
                    response.sendError(500, "Resultado inesperado: " + res);
                    return;
            }
        }

        if ("cerrarRemota".equals(accion)) {
            // POST: el usuario pide cerrar la sesión de un perfil activo en OTRA ventana
            // (disparado desde el modal de rechazo PERFIL_YA_ACTIVO o PLAN_LLENO)
            Integer idPerfilCerrar;
            try {
                idPerfilCerrar = Integer.parseInt(request.getParameter("idPerfil"));
            } catch (NumberFormatException ex) {
                response.sendError(400, "idPerfil inválido");
                return;
            }
            Perfil pCerrar = perfilF.findConCuenta(idPerfilCerrar);
            if (pCerrar == null
                    || pCerrar.getCuenta() == null
                    || !pCerrar.getCuenta().getIdcuenta().equals(cuenta.getIdcuenta())) {
                request.setAttribute("motivoRechazo", "INVALIDO");
                request.setAttribute("mensajeRechazo",
                        "No podés cerrar ese perfil: no pertenece a tu cuenta.");
                listarYForward(request, response, cuenta);
                return;
            }
            // Verificar que esté realmente activo
            List<Integer> activosAhora = sesionF.perfilesActivosDeCuenta(cuenta.getIdcuenta());
            if (!activosAhora.contains(idPerfilCerrar)) {
                request.setAttribute("motivoRechazo", "YA_LIBRE");
                request.setAttribute("mensajeRechazo",
                        "El perfil '" + pCerrar.getNombre() + "' ya no tenía sesión activa.");
                listarYForward(request, response, cuenta);
                return;
            }
            // >>> @Lock(WRITE) <<<
            sesionF.logout(pCerrar);
            // Confirmación estructurada para mostrar en modal de éxito
            request.setAttribute("motivoExito", "REMIT_CERRADA");
            request.setAttribute("mensajeExito",
                    "Sesión remota de '" + pCerrar.getNombre() + "' cerrada. " +
                    "Ya podés elegir ese perfil para entrar acá.");
            listarYForward(request, response, cuenta);
            return;
        }

        // GET: mostrar pantalla de selección
        listarYForward(request, response, cuenta);
    }

    private void listarYForward(HttpServletRequest request,
                                HttpServletResponse response,
                                Cuenta cuenta) throws ServletException, IOException {
        List<Perfil> perfiles = perfilF.findByCuenta(cuenta.getIdcuenta());
        List<Integer> activos = sesionF.perfilesActivosDeCuenta(cuenta.getIdcuenta());

        request.setAttribute("perfiles", perfiles);
        request.setAttribute("perfilesActivos", activos);
        request.setAttribute("maxPerfiles", cuenta.getPlan().getMaxPerfiles());

        // Si la cuenta NO llegó al máximo de perfiles, mostrar botón "agregar perfil"
        Long count = perfilF.countByCuenta(cuenta.getIdcuenta());
        request.setAttribute("puedeAgregarPerfil",
                count < cuenta.getPlan().getMaxPerfiles());

        request.getRequestDispatcher("/WEB-INF/vista/seleccionPerfil.jsp")
               .forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
