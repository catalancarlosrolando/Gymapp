<%-- 
    Document   : footer
    Created on : 2 nov 2026, 15:04:47
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<footer class="footer">
    <div class="footer-grid">
        <div class="footer-col">
            <div class="footer-brand">GimnasioApp</div>
            <p style="margin-top: var(--space-3); color: var(--color-text-secondary);">
                Plataforma de gestión de rutinas con control de planes estilo Netflix.
                Entrená cuando quieras, donde quieras.
            </p>
        </div>
        <div class="footer-col">
            <h4>Plataforma</h4>
            <ul>
                <li><a href="Home">Inicio</a></li>
                <li><a href="Rutinas">Mis rutinas</a></li>
                <li><a href="ListarEjercicios">Catálogo</a></li>
            </ul>
        </div>
        <div class="footer-col">
            <h4>Cuenta</h4>
            <ul>
                <li><a href="Perfil">Mi perfil</a></li>
                <li><a href="GestionPerfiles">Gestionar perfiles</a></li>
                <li><a href="CambiarPerfil">Cambiar perfil</a></li>
            </ul>
        </div>
        <div class="footer-col">
            <h4>Soporte</h4>
            <ul>
                <li><a href="#">Planes y precios</a></li>
                <li><a href="#">Preguntas frecuentes</a></li>
                <li><a href="#">Contacto</a></li>
            </ul>
        </div>
    </div>
    <div class="footer-bottom">
        <span>&copy; 2025 GimnasioApp &middot; Jakarta EE 11 + GlassFish 8 + MySQL 8</span>
        <span>v2.0 &middot; UI/UX basado en Nike Training Club</span>
    </div>
</footer>

