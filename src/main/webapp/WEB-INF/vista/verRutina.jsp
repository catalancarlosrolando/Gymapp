<%-- 
    Document   : verRutina
    Created on : 2 nov 2026, 15:08:45
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${rutina.nombre} - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="ver-rutina">
        <a href="Rutinas" class="back-link">&larr; Volver a rutinas</a>

        <header class="rutina-header">
            <img class="rutina-hero" src="${rutina.imagen}" alt="${rutina.nombre}">
            <div class="rutina-meta">
                <span class="tag tag-${rutina.categoría}">${rutina.categoría}</span>
                <h1>${rutina.nombre}</h1>
                <p>${rutina.descripción}</p>
                <p class="muted small">
                    Tiempo total: <strong>${rutina.tiempo}</strong> segundos
                    &middot; Perfil: <strong>${perfilActivo.nombre}</strong>
                </p>
                <c:if test="${rutina.realizada}">
                    <span class="badge badge-success">Realizada</span>
                </c:if>
                <div class="rutina-actions">
                    <c:if test="${!rutina.realizada}">
                        <a href="CompletarRutina?id=${rutina.idrutina}" class="btn btn-success btn-sm">
                            Marcar como realizada
                        </a>
                    </c:if>
                    <a href="EliminarRutina?id=${rutina.idrutina}" class="btn btn-danger btn-sm"
                       onclick="return confirm('¿Eliminar esta rutina?')">Eliminar</a>
                </div>
            </div>
        </header>

        <section>
            <div class="section-header">
                <h2>Ejercicios (${fn:length(detalles)})</h2>
            </div>

            <c:choose>
                <c:when test="${empty detalles}">
                    <div class="empty-state">
                        <p>Esta rutina no tiene ejercicios asociados todavía.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="ejercicios-cards">
                        <c:forEach var="d" items="${detalles}" varStatus="loop">
                            <div class="ejercicio-card">
                                <img src="${d.ejercicio.imagen}" alt="${d.ejercicio.nombre}">
                                <div class="ejercicio-card-body">
                                    <h3>${loop.index + 1}. ${d.ejercicio.nombre}</h3>
                                    <p class="muted small">${d.ejercicio.descripción}</p>
                                    <div class="ejercicio-stats">
                                        <span>Series: ${d.series}</span>
                                        <span>Repes: ${d.repeticiones}</span>
                                        <span>Peso: ${d.peso} kg</span>
                                        <span>Tiempo: ${d.tiempo}s</span>
                                        <span>Descanso: ${d.descanso}s</span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>


