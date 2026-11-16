<%-- 
    Document   : rutinas
    Created on : 2 nov 2026, 15:07:57
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
    <title>Mis rutinas - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="rutinas">
        <section class="section-header">
            <h1>Rutinas de ${perfilActivo.nombre}</h1>
            <a href="SolicitarRutina" class="btn btn-primary btn-sm">+ Crear rutina</a>
        </section>

        <c:choose>
            <c:when test="${empty rutinas}">
                <div class="empty-state">
                    <h2 style="margin-bottom: var(--space-2);">Aún no tenés rutinas para este perfil.</h2>
                    <p style="margin-bottom: var(--space-6);">Creá tu primera rutina en menos de un minuto.</p>
                    <a href="SolicitarRutina" class="btn btn-primary">Crear la primera rutina</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="cards">
                    <c:forEach var="r" items="${rutinas}">
                        <div class="card ${r.realizada ? 'card-done' : ''}">
                            <img class="card-image" src="${r.imagen}" alt="${r.nombre}">
                            <div class="card-body">
                                <h3>${r.nombre}</h3>
                                <span class="tag tag-${r.categoría}">${r.categoría}</span>
                                <p>${r.descripción}</p>
                                <c:if test="${r.realizada}">
                                    <span class="badge badge-success">Realizada</span>
                                </c:if>
                                <div class="card-actions">
                                    <a href="VerRutina?id=${r.idrutina}" class="btn btn-secondary btn-sm">Ver</a>
                                    <c:if test="${!r.realizada}">
                                        <a href="CompletarRutina?id=${r.idrutina}" class="btn btn-success btn-sm">Marcar</a>
                                    </c:if>
                                    <a href="EliminarRutina?id=${r.idrutina}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('¿Eliminar la rutina ${r.nombre}?')">Eliminar</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>


