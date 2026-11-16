<%-- 
    Document   : listarEjercicios
    Created on : 2 nov 2026, 15:07:35
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Catálogo de ejercicios - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="ejercicios">
        <section class="section-header">
            <h1>Catálogo de ejercicios</h1>
        </section>

        <div class="cards">
            <c:forEach var="e" items="${ejercicios}">
                <div class="card card-ejercicio">
                    <img src="${e.imagen}" alt="${e.nombre}">
                    <div class="card-body">
                        <h3>${e.nombre}</h3>
                        <div class="ejercicio-stats">
                            <span>Series: <strong>${e.series}</strong></span>
                            <span>Repes: <strong>${e.repeticiones}</strong></span>
                            <span>Peso: <strong>${e.peso} kg</strong></span>
                            <span>Descanso: <strong>${e.descanso} s</strong></span>
                        </div>
                        <p class="muted small">${e.descripción}</p>
                    </div>
                </div>
            </c:forEach>
        </div>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>

