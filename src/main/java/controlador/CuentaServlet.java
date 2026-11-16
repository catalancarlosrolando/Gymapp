/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Cuenta;
import entidad.Plan;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import sesiones.CuentaFacade;
import sesiones.PlanFacade;

/**
 *
 * @author carlos
 */
@WebServlet(
    name = "CuentaServlet",
    loadOnStartup = 9,
    urlPatterns = { "/ListarCuentas", "/AgregarCuenta" }
)
public class CuentaServlet extends HttpServlet {

    @EJB
    private CuentaFacade cuentaF;

    @EJB
    private PlanFacade planF;

    
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String path = request.getServletPath();

        switch (path) {

            case "/ListarCuentas": {
                List<Cuenta> cuentas = cuentaF.findAll();
                List<Plan> planes = planF.findAll();
                request.setAttribute("cuentas", cuentas);
                request.setAttribute("planes", planes);
                request.getRequestDispatcher("/WEB-INF/vista/listarCuentas.jsp")
                       .forward(request, response);
                return;
            }

            case "/AgregarCuenta": {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String idPlanStr = request.getParameter("idPlan");
                if (username == null || username.isEmpty()
                        || password == null || password.isEmpty()
                        || idPlanStr == null) {
                    response.sendError(400, "Faltan parámetros");
                    return;
                }
                Integer idPlan = Integer.parseInt(idPlanStr);
                Plan plan = planF.find(idPlan);
                if (plan == null) {
                    response.sendError(400, "Plan inexistente");
                    return;
                }
                Cuenta c = new Cuenta();
                c.setUsername(username);
                c.setPassword(password);
                c.setPlan(plan);
                cuentaF.create(c);
                response.sendRedirect("ListarCuentas");
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
