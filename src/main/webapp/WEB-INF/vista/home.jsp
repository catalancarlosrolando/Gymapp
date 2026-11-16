<%-- 
    Document   : home
    Created on : 2 nov 2026, 15:06:50
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
    <meta name="description" content="Tus rutinas pendientes en GimnasioApp.">
    <title>GimnasioApp - Inicio</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="home">
        <section class="home-hero" aria-label="Bienvenida">
            <img src="${perfilActivo.imagen}" alt="" aria-hidden="true">
            <div class="home-hero-overlay">
                <h1>Hola, ${perfilActivo.nombre}</h1>
            </div>
        </section>

        <section class="bienvenida">
            <p>
                Cuenta: <strong>${cuentaLogueada.username}</strong>
                &middot; Plan <strong>${cuentaLogueada.plan.nombre}</strong>
                &middot; ${cuentaLogueada.plan.cupos} sesiones simultáneas
            </p>
        </section>

        <section>
            <div class="section-header">
                <h2>Tus rutinas pendientes</h2>
                <a href="SolicitarRutina" class="btn btn-primary btn-sm">+ Crear rutina</a>
            </div>

            <c:choose>
                <c:when test="${empty pendientes}">
                    <div class="empty-state">
                        <p>No tenés rutinas pendientes. ¡Buen trabajo!</p>
                        <a href="SolicitarRutina" class="btn btn-secondary" style="margin-top: var(--space-4);">Crear una rutina</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="cards">
                        <c:forEach var="r" items="${pendientes}">
                            <a class="card" href="VerRutina?id=${r.idrutina}">
                                <img class="card-image" src="${r.imagen}" alt="${r.nombre}">
                                <div class="card-body">
                                    <h3>${r.nombre}</h3>
                                    <span class="tag tag-${r.categoría}">${r.categoría}</span>
                                    <p>${r.descripción}</p>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section style="margin-top: var(--space-12);">
            <div class="section-header">
                <h2>Acciones rápidas</h2>
            </div>
            <div class="quick-actions">
                <a href="Rutinas" class="action-card">
                    <span class="action-icon" aria-hidden="true">&#128170;</span>
                    <strong>Ver todas mis rutinas</strong>
                </a>
                <a href="ListarEjercicios" class="action-card">
                    <span class="action-icon" aria-hidden="true">&#127947;</span>
                    <strong>Catálogo de ejercicios</strong>
                </a>
                <a href="Perfil" class="action-card">
                    <span class="action-icon" aria-hidden="true">&#128100;</span>
                    <strong>Mi perfil</strong>
                </a>
                <a href="GestionPerfiles" class="action-card">
                    <span class="action-icon" aria-hidden="true">&#128101;</span>
                    <strong>Gestionar perfiles (${cuentaLogueada.plan.maxPerfiles})</strong>
                </a>
            </div>
        </section>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>

