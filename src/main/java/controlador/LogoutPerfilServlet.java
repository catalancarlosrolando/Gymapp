/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Perfil;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import sesiones.SesionActivaFacade;

/**
 * LogoutServlet - Cierra sesión del perfil activo.
 * @author carlos
 */
@WebServlet(
    name = "LogoutPerfilServlet",
    loadOnStartup = 3,
    urlPatterns = { "/LogoutPerfil", "/CambiarPerfil", "/CerrarSesionBeacon" }
)
public class LogoutPerfilServlet extends HttpServlet {

    @EJB
    private SesionActivaFacade sesionF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Endpoint para el beacon del navegador: cuando se cierra la pestaña
        // el JS llama a POST /CerrarSesionBeacon. El navegador garantiza que
        // el request llega aunque la pestaña ya esté cerrada (sendBeacon API).
        // No requiere body: el servidor identifica la sesión por la cookie JSESSIONID.
        if ("/CerrarSesionBeacon".equals(path)) {
            HttpSession session = request.getSession(false);
            Perfil p = (session != null)
                     ? (Perfil) session.getAttribute("perfilActivo")
                     : null;
            if (p != null) {
                sesionF.logout(p);   // >>> @Lock(WRITE) <<< libera BD + cache
                session.removeAttribute("perfilActivo");
            }
            // 204 No Content: el beacon no espera respuesta
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Logout normal (GET) o CambiarPerfil
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);

        Perfil perfil = (session != null)
                      ? (Perfil) session.getAttribute("perfilActivo")
                      : null;

        if (perfil != null) {
            sesionF.logout(perfil);   // >>> @Lock(WRITE) <<<
            session.removeAttribute("perfilActivo");
        }

        if ("/CambiarPerfil".equals(path)) {
            // Volver a la pantalla de selección (sin cerrar sesión de cuenta)
            response.sendRedirect("SeleccionPerfil");
        } else {
            // Logout completo: invalida sesión HTTP y vuelve al login
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("Login");
        }
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
