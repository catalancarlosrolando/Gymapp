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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import sesiones.PerfilFacade;

/**
 * Edición del perfil activo.
 * @author carlos
 */
@WebServlet(
    name = "PerfilServlet",
    loadOnStartup = 7,
    urlPatterns = { "/Perfil", "/ModificarPerfil" }
)
public class PerfilServlet extends HttpServlet {

    @EJB
    private PerfilFacade perfilF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        Perfil perfil = (session != null)
                      ? (Perfil) session.getAttribute("perfilActivo")
                      : null;
        if (perfil == null) {
            response.sendRedirect("Login");
            return;
        }

        String path = request.getServletPath();

        switch (path) {

            case "/Perfil": {
                // recargar desde BD para tener datos frescos
                Perfil pDB = perfilF.findConCuenta(perfil.getIdperfil());
                if (pDB != null) {
                    session.setAttribute("perfilActivo", pDB);
                    perfil = pDB;
                }
                request.setAttribute("perfil", perfil);
                request.getRequestDispatcher("/WEB-INF/vista/perfil.jsp")
                       .forward(request, response);
                return;
            }

            case "/ModificarPerfil": {
                Perfil pDB = perfilF.find(perfil.getIdperfil());
                if (pDB == null) {
                    response.sendError(404, "Perfil no encontrado");
                    return;
                }
                pDB.setNombre(request.getParameter("nombre"));
                pDB.setEmail(request.getParameter("email"));
                String fnac = request.getParameter("fechanacimiento");
                if (fnac != null && !fnac.isEmpty()) {
                    try {
                        pDB.setFechanacimiento(
                            new SimpleDateFormat("yyyy-MM-dd").parse(fnac));
                    } catch (ParseException ex) {
                        // dejar como estaba
                    }
                }
                pDB.setGenero(request.getParameter("genero"));
                pDB.setTema(request.getParameter("tema"));
                String imagen = request.getParameter("imagen");
                if (imagen != null && !imagen.isEmpty()) {
                    pDB.setImagen(imagen);
                }
                perfilF.edit(pDB);

                session.setAttribute("perfilActivo", pDB);
                response.sendRedirect("Perfil");
                return;
            }

            default:
                response.sendError(404, "URL no soportada: " + path);
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
