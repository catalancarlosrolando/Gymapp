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
 *
 * @author carlos
 */
@WebServlet(
    name = "GestionPerfilesServlet",
    loadOnStartup = 8,
    urlPatterns = { "/GestionPerfiles" }
)
public class GestionPerfilesServlet extends HttpServlet {

    @EJB
    private PerfilFacade perfilF;

    @EJB
    private SesionActivaFacade sesionF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        Cuenta cuenta = (session != null)
                      ? (Cuenta) session.getAttribute("cuentaLogueada")
                      : null;
        if (cuenta == null) {
            response.sendRedirect("Login");
            return;
        }

        String accion = request.getParameter("accion");

        if (accion == null) {
            // GET: listar
            listarPerfiles(request, response, cuenta);
            return;
        }

        switch (accion) {

            case "agregar": {
                // Validar que la cuenta no supere max_perfiles
                Long count = perfilF.countByCuenta(cuenta.getIdcuenta());
                if (count >= cuenta.getPlan().getMaxPerfiles()) {
                    request.setAttribute("error",
                            "Tu plan " + cuenta.getPlan().getNombre() +
                            " permite hasta " + cuenta.getPlan().getMaxPerfiles() +
                            " perfiles. Upgradeá a un plan superior para crear más.");
                    listarPerfiles(request, response, cuenta);
                    return;
                }

                Perfil p = new Perfil();
                p.setNombre(request.getParameter("nombre"));
                p.setEmail(request.getParameter("email"));
                p.setGenero(request.getParameter("genero"));
                p.setTema(request.getParameter("tema"));
                String imagen = request.getParameter("imagen");
                if (imagen != null && !imagen.isEmpty()) {
                    p.setImagen(imagen);
                } else {
                    p.setImagen("/Web-App/img/avatar-default.png");
                }
                p.setCuenta(cuenta);
                perfilF.create(p);

                request.setAttribute("info",
                        "Perfil '" + p.getNombre() + "' creado correctamente.");
                listarPerfiles(request, response, cuenta);
                return;
            }

            case "editar": {
                Integer idPerfil = parseIntSafe(request.getParameter("idPerfil"));
                Perfil p = perfilF.find(idPerfil);
                if (p == null
                        || p.getCuenta() == null
                        || !p.getCuenta().getIdcuenta().equals(cuenta.getIdcuenta())) {
                    response.sendError(403, "No podés editar ese perfil");
                    return;
                }
                p.setNombre(request.getParameter("nombre"));
                p.setEmail(request.getParameter("email"));
                p.setGenero(request.getParameter("genero"));
                p.setTema(request.getParameter("tema"));
                String imagen = request.getParameter("imagen");
                if (imagen != null && !imagen.isEmpty()) {
                    p.setImagen(imagen);
                }
                perfilF.edit(p);

                // Si era el perfil activo, refrescar la sesión HTTP
                Perfil activo = (Perfil) session.getAttribute("perfilActivo");
                if (activo != null && activo.getIdperfil().equals(idPerfil)) {
                    session.setAttribute("perfilActivo", p);
                }
                request.setAttribute("info", "Perfil actualizado.");
                listarPerfiles(request, response, cuenta);
                return;
            }

            case "eliminar": {
                Integer idPerfil = parseIntSafe(request.getParameter("idPerfil"));
                Perfil p = perfilF.find(idPerfil);
                if (p == null
                        || p.getCuenta() == null
                        || !p.getCuenta().getIdcuenta().equals(cuenta.getIdcuenta())) {
                    response.sendError(403, "No podés eliminar ese perfil");
                    return;
                }
                // No permitir eliminar si tiene sesión activa
                List<Integer> activos = sesionF.perfilesActivosDeCuenta(cuenta.getIdcuenta());
                if (activos.contains(idPerfil)) {
                    request.setAttribute("error",
                            "No podés eliminar un perfil que tiene sesión activa. " +
                            "Cerrá sesión primero.");
                    listarPerfiles(request, response, cuenta);
                    return;
                }
                // No permitir eliminar el último perfil
                Long count = perfilF.countByCuenta(cuenta.getIdcuenta());
                if (count <= 1) {
                    request.setAttribute("error",
                            "Debe quedar al menos un perfil en la cuenta.");
                    listarPerfiles(request, response, cuenta);
                    return;
                }
                perfilF.remove(p);
                request.setAttribute("info", "Perfil eliminado.");
                listarPerfiles(request, response, cuenta);
                return;
            }

            default:
                response.sendError(404, "Acción no soportada: " + accion);
        }
    }

    private void listarPerfiles(HttpServletRequest request,
                                HttpServletResponse response,
                                Cuenta cuenta) throws ServletException, IOException {
        List<Perfil> perfiles = perfilF.findByCuenta(cuenta.getIdcuenta());
        Long count = perfilF.countByCuenta(cuenta.getIdcuenta());
        request.setAttribute("perfiles", perfiles);
        request.setAttribute("countPerfiles", count);
        request.setAttribute("maxPerfiles", cuenta.getPlan().getMaxPerfiles());
        request.setAttribute("puedeAgregar",
                count < cuenta.getPlan().getMaxPerfiles());
        request.getRequestDispatcher("/WEB-INF/vista/gestionarPerfiles.jsp")
               .forward(request, response);
    }

    private Integer parseIntSafe(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
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
