/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import entidad.Ejercicio;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import sesiones.EjercicioFacade;
/**
 *
 * @author carlos
 */
@WebServlet(
    name = "EjercicioServlet",
    loadOnStartup = 6,
    urlPatterns = { "/ListarEjercicios", "/AgregarEjercicio" }
)
public class EjercicioServlet extends HttpServlet {

    @EJB
    private EjercicioFacade ejercicioF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String path = request.getServletPath();

        switch (path) {

            case "/ListarEjercicios": {
                List<Ejercicio> ejercicios = ejercicioF.findAll();
                request.setAttribute("ejercicios", ejercicios);
                request.getRequestDispatcher("/WEB-INF/vista/listarEjercicios.jsp")
                       .forward(request, response);
                return;
            }

            case "/AgregarEjercicio": {
                // POST: alta ejercicio
                Ejercicio e = new Ejercicio();
                e.setNombre(request.getParameter("nombre"));
                e.setImagen(request.getParameter("imagen"));
                e.setTiempo(parseIntSafe(request.getParameter("tiempo")));
                e.setSeries(parseIntSafe(request.getParameter("series")));
                e.setRepeticiones(parseIntSafe(request.getParameter("repeticiones")));
                String pesoStr = request.getParameter("peso");
                if (pesoStr != null && !pesoStr.isEmpty()) {
                    e.setPeso(new java.math.BigDecimal(pesoStr));
                }
                e.setDescanso(parseIntSafe(request.getParameter("descanso")));
                e.setDescripción(request.getParameter("descripcion"));
                ejercicioF.create(e);
                response.sendRedirect("ListarEjercicios");
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
