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
import sesiones.RutinaFacade;

/**
 *
 * @author carlos
 */
@WebServlet(
    name = "HomeServlet",
    loadOnStartup = 4,
    urlPatterns = { "/Home" }
)
public class HomeServlet extends HttpServlet {

    @EJB
    private RutinaFacade rutinaF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);

        Perfil perfil = (session != null)
                      ? (Perfil) session.getAttribute("perfilActivo")
                      : null;
        Cuenta cuenta = (session != null)
                      ? (Cuenta) session.getAttribute("cuentaLogueada")
                      : null;

        if (perfil == null) {
            if (cuenta != null) {
                response.sendRedirect("SeleccionPerfil");
            } else {
                response.sendRedirect("Login");
            }
            return;
        }

        // Cargar rutinas pendientes del perfil
        List<entidad.Rutina> pendientes = rutinaF.findPendientesByPerfil(perfil.getIdperfil());
        request.setAttribute("pendientes", pendientes);
        // Cupos ocupados del plan (para mostrar "X de Y en uso")
        // se podría inyectar SesionActivaFacade, lo dejamos como extensión

        request.getRequestDispatcher("/WEB-INF/vista/home.jsp")
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
