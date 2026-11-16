/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Cuenta;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import sesiones.CuentaFacade;

/**
 * LoginServlet - Validación de credenciales.
 * @author carlos
 */
@WebServlet(
    name = "LoginServlet",
    loadOnStartup = 1,
    urlPatterns = { "/Login", "/Logout" }
)
public class LoginServlet extends HttpServlet {

    @EJB
    private CuentaFacade cuentaF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String path = request.getServletPath();
        String url = null;

        switch (path) {

            case "/Login":
                String accion = request.getParameter("accion");

                if ("entrar".equals(accion)) {
                    // POST del formulario
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");

                    Cuenta c = cuentaF.buscarPorCredenciales(username, password);
                    if (c == null) {
                        request.setAttribute("error",
                                "Usuario o contraseña incorrectos.");
                        url = "/index.jsp";
                        break;
                    }

                    // Login OK: guardar CUENTA (no perfil todavía) en sesión HTTP
                    HttpSession session = request.getSession(true);
                    session.setAttribute("cuentaLogueada", c);

                    // Redirigir a la pantalla de selección de perfil (easter egg)
                    response.sendRedirect("SeleccionPerfil");
                    return;
                }

                // GET: mostrar formulario de login
                url = "/index.jsp";
                break;

            case "/Logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect("Login");
                return;

            default:
                response.sendError(404, "URL no soportada: " + path);
                return;
        }

        request.getRequestDispatcher(url).forward(request, response);
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
