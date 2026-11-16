<%-- 
    Document   : header
    Created on : 2 nov 2026, 15:04:55
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="navbar">
    <div class="brand">
        <a href="${ctx}/Home">GimnasioApp</a>
    </div>
    <nav aria-label="Navegación principal">
        <a href="${ctx}/Home">Inicio</a>
        <a href="${ctx}/Rutinas">Rutinas</a>
        <a href="${ctx}/ListarEjercicios">Ejercicios</a>
        <a href="${ctx}/Perfil">Perfil</a>
        <a href="${ctx}/GestionPerfiles">Perfiles</a>
        <a href="${ctx}/CambiarPerfil" class="nav-profile-switch">
            <img src="${ctx}/${perfilActivo.imagen}" alt="${perfilActivo.nombre}" class="nav-avatar">
            ${perfilActivo.nombre}
        </a>
        <a href="${ctx}/Logout" class="nav-logout">Salir</a>
    </nav>
</header>

<%-- Script de detección de cierre de pestaña/navegador.
     Solo se activa si hay un perfilActivo en sesión HTTP.
     Usa navigator.sendBeacon (API moderna) para garantizar que el request
     llegue al servidor incluso si la pestaña ya se cerró.

     Lógica:
       1. Marca navegacionInterna=true al hacer click en cualquier <a> o submit de <form>.
       2. En pagehide (evento que se dispara al cerrar pestaña o navegar a otro sitio),
          si NO fue navegación interna, envía el beacon a /CerrarSesionBeacon.
       3. El servidor identifica la sesión por la cookie JSESSIONID y libera el cupo.
--%>
<c:if test="${not empty perfilActivo}">
<script>
(function() {
    'use strict';
    var navegacionInterna = false;

    document.addEventListener('click', function(e) {
        var el = e.target;
        while (el && el !== document.body) {
            if (el.tagName === 'A' && el.href && el.href.indexOf(location.origin) === 0) {
                navegacionInterna = true;
                return;
            }
            if (el.tagName === 'BUTTON' && (el.type === 'submit' || el.type === 'button')) {
                navegacionInterna = true;
                return;
            }
            el = el.parentElement;
        }
    }, true);

    document.addEventListener('submit', function() {
        navegacionInterna = true;
    }, true);

    function enviarBeaconCerrarSesion() {
        if (navegacionInterna) return;
        if (!navigator.sendBeacon) return;
        try {
            navigator.sendBeacon('${ctx}/CerrarSesionBeacon', '');
        } catch (e) {}
    }

    window.addEventListener('pagehide', enviarBeaconCerrarSesion);
    window.addEventListener('beforeunload', enviarBeaconCerrarSesion);
})();
</script>
</c:if>


