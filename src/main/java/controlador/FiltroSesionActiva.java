/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import entidad.Cuenta;
import entidad.Perfil;
import jakarta.ejb.EJB;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import sesiones.SesionActivaFacade;

/**
 *
 * @author carlos
 */
@WebFilter(
    urlPatterns = {
        "/Home",
        "/Rutinas", "/SolicitarRutina", "/CrearRutina", "/VerRutina",
        "/CompletarRutina", "/EliminarRutina",
        "/Perfil", "/ModificarPerfil",
        "/ListarEjercicios", "/AgregarEjercicio",
        "/GestionPerfiles",
        "/CambiarPerfil"
    }
)
public class FiltroSesionActiva implements Filter {

    @EJB
    private SesionActivaFacade sesionF;

    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        Perfil perfil = (session != null)
                      ? (Perfil) session.getAttribute("perfilActivo")
                      : null;
        Cuenta cuenta = (session != null)
                      ? (Cuenta) session.getAttribute("cuentaLogueada")
                      : null;

        // Si no hay cuenta logueada, dejamos pasar (lo rechazará el servlet destino)
        if (cuenta == null) {
            chain.doFilter(req, resp);
            return;
        }

        // Si hay cuenta pero todavía no eligió perfil (está en pantalla de selección),
        // dejamos pasar (todavía no hay sesión activa que validar)
        if (perfil == null) {
            chain.doFilter(req, resp);
            return;
        }

        // Hay perfilActivo en HttpSession -> verificar si sigue activo en el facade
        if (!sesionF.sigueActivo(perfil)) {
            // La sesión fue cerrada remotamente desde otra ventana
            session.removeAttribute("perfilActivo");
            session.invalidate();
            // Re-crear sesión mínima para flashear el mensaje
                        // La sesión fue cerrada remotamente desde otra ventana.
            // IMPORTANTE: usar REQUEST scope + FORWARD directo (NO session scope + redirect).
            // Con redirect, la sesión nueva se pierde entre el browser y el contenedor
            // (race condition de cookies) y el mensaje nunca llega al JSP.
            // Con forward directo, el request scope sobrevive hasta el index.jsp.
            request.setAttribute("mensajeSesionRemota",
                    "Tu sesión del perfil '" + perfil.getNombre() +
                    "' fue cerrada desde otra ventana. Volvé a ingresar.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() { }
}


