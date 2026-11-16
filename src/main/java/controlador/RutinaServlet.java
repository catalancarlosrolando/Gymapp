/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Ejercicio;
import entidad.Perfil;
import entidad.Rutina;
import entidad.RutinaHasEjercicio;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import sesiones.EjercicioFacade;
import sesiones.RutinaFacade;
import sesiones.RutinaHasEjercicioFacade;

/**
 * CRUD completo de rutinas del perfil activo
 * @author carlos
 */
@WebServlet(
    name = "RutinaServlet",
    loadOnStartup = 5,
    urlPatterns = {
        "/Rutinas",
        "/SolicitarRutina",
        "/CrearRutina",
        "/VerRutina",
        "/CompletarRutina",
        "/EliminarRutina"
    }
)
public class RutinaServlet extends HttpServlet {

    @EJB
    private RutinaFacade rutinaF;

    @EJB
    private EjercicioFacade ejercicioF;

    @EJB
    private RutinaHasEjercicioFacade rheF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String path = request.getServletPath();

        HttpSession session = request.getSession(false);
        Perfil perfil = (session != null)
                      ? (Perfil) session.getAttribute("perfilActivo")
                      : null;
        if (perfil == null) {
            response.sendRedirect("Login");
            return;
        }

        switch (path) {

            case "/Rutinas": {
                List<Rutina> rutinas = rutinaF.findByPerfil(perfil.getIdperfil());
                request.setAttribute("rutinas", rutinas);
                request.getRequestDispatcher("/WEB-INF/vista/rutinas.jsp")
                       .forward(request, response);
                return;
            }

            case "/SolicitarRutina": {
                // mostrar formulario con catálogo de ejercicios para el modal
                List<Ejercicio> ejercicios = ejercicioF.findAll();
                request.setAttribute("ejercicios", ejercicios);
                request.getRequestDispatcher("/WEB-INF/vista/crearRutina.jsp")
                       .forward(request, response);
                return;
            }

            case "/CrearRutina": {
                // POST: crear rutina + ejercicios
                String nombre = request.getParameter("nombre");
                String categoria = request.getParameter("categoria");
                String imagen = request.getParameter("imagen");
                String tiempoStr = request.getParameter("tiempo");
                String descripcion = request.getParameter("descripcion");
                Integer tiempo = (tiempoStr != null && !tiempoStr.isEmpty())
                               ? Integer.parseInt(tiempoStr) : null;

                Rutina r = new Rutina();
                r.setNombre(nombre);
                r.setCategoría(categoria);
                r.setImagen((imagen != null && !imagen.isEmpty())
                          ? imagen : "Web-App/img/r-default.png");
                r.setTiempo(tiempo);
                r.setDescripción(descripcion);
                r.setRealizada(false);
                r.setPerfil(perfil);

                // IMPORTANTE: usar createConFlush para garantizar que
                // r.getIdrutina() esté cargado ANTES de agregar ejercicios.
                // Si usás rutinaF.create(r), el ID puede ser null todavía
                // y el INSERT de rutina_has_ejercicio falla con FK null.
                rutinaF.createConFlush(r);

                log(">>> Rutina creada: id=" + r.getIdrutina()
                  + " nombre=" + r.getNombre()
                  + " perfil=" + perfil.getIdperfil());

                // Procesar ejercicios asociados (parámetros ejercicio_<id>_series ...)
                int ejerciciosAgregados = 0;
                java.util.Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()) {
                    String p = params.nextElement();
                    if (p.startsWith("ejercicio_") && p.endsWith("_series")) {
                        String idStr = p.substring("ejercicio_".length(),
                                                   p.length() - "_series".length());
                        Integer idEj;
                        try { idEj = Integer.parseInt(idStr); }
                        catch (NumberFormatException ex) {
                            log(">>> Param con id no parseable: " + p);
                            continue;
                        }

                        Integer series = parseIntSafe(request.getParameter(p));
                        Integer repes  = parseIntSafe(request.getParameter(
                                       "ejercicio_" + idStr + "_repeticiones"));
                        BigDecimal peso = parseBigDecimalSafe(request.getParameter(
                                       "ejercicio_" + idStr + "_peso"));
                        Integer tEjer   = parseIntSafe(request.getParameter(
                                       "ejercicio_" + idStr + "_tiempo"));
                        Integer descanso = parseIntSafe(request.getParameter(
                                       "ejercicio_" + idStr + "_descanso"));

                        log(">>> Agregando ejercicio " + idEj
                          + " a rutina " + r.getIdrutina()
                          + " series=" + series + " repes=" + repes
                          + " peso=" + peso);

                        try {
                            rheF.agregarEjercicio(r.getIdrutina(), idEj,
                                                  series, repes, peso, tEjer, descanso);
                            ejerciciosAgregados++;
                        } catch (Exception ex) {
                            log(">>> ERROR agregando ejercicio " + idEj
                              + " a rutina " + r.getIdrutina() + ": " + ex.getMessage());
                        }
                    }
                }

                log(">>> Total ejercicios agregados: " + ejerciciosAgregados);
                response.sendRedirect("Rutinas");
                return;
            }

            case "/VerRutina": {
                Integer idRutina = parseIntSafe(request.getParameter("id"));
                Rutina r = rutinaF.findConEjercicios(idRutina);
                if (r == null || r.getPerfil() == null
                        || !r.getPerfil().getIdperfil().equals(perfil.getIdperfil())) {
                    response.sendError(403, "Rutina no accesible");
                    return;
                }
                List<RutinaHasEjercicio> detalles = rheF.findByRutina(idRutina);
                request.setAttribute("rutina", r);
                request.setAttribute("detalles", detalles);
                request.getRequestDispatcher("/WEB-INF/vista/verRutina.jsp")
                       .forward(request, response);
                return;
            }

            case "/CompletarRutina": {
                Integer idRutina = parseIntSafe(request.getParameter("id"));
                Rutina r = rutinaF.find(idRutina);
                if (r != null && r.getPerfil() != null
                        && r.getPerfil().getIdperfil().equals(perfil.getIdperfil())) {
                    r.setRealizada(true);
                    rutinaF.edit(r);
                }
                response.sendRedirect("Home");
                return;
            }

            case "/EliminarRutina": {
                Integer idRutina = parseIntSafe(request.getParameter("id"));
                Rutina r = rutinaF.find(idRutina);
                if (r != null && r.getPerfil() != null
                        && r.getPerfil().getIdperfil().equals(perfil.getIdperfil())) {
                    rutinaF.remove(r);
                }
                response.sendRedirect("Rutinas");
                return;
            }

            default:
                response.sendError(404, "URL no soportada: " + path);
        }
    }

    private Integer parseIntSafe(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }

    private BigDecimal parseBigDecimalSafe(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return new BigDecimal(s); } catch (NumberFormatException ex) { return null; }
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
