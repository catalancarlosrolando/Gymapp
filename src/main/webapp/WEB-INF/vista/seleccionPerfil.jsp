<%-- 
    Document   : seleccionPerfil
    Created on : 2 nov 2026, 15:08:27
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Elegí quién entrena hoy en GimnasioApp.">
    <title>¿Quién entrena hoy? - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
    <link rel="stylesheet" href="Web-App/css/seleccionPerfil.css">
</head>
<body class="profiles-page">
    <header class="navbar navbar-dark">
        <div class="brand"><a href="${ctx}/Home">GimnasioApp</a></div>
        <nav>
            <a href="${ctx}/GestionPerfiles">Gestionar perfiles</a>
            <a href="${ctx}/Logout">Cerrar sesión</a>
        </nav>
    </header>

    <main class="profiles-main">
        <section class="profiles-header">
            <h1>¿Quién entrena hoy?</h1>
            <p>
                Cuenta: <strong>${cuentaLogueada.username}</strong>
                &middot; Plan <strong>${cuentaLogueada.plan.nombre}</strong>
                &middot; ${fn:length(perfiles)}/${maxPerfiles} perfiles creados
                &middot; ${cuentaLogueada.plan.cupos} sesiones simultáneas
            </p>
        </section>

        <section class="profile-cards">
            <c:forEach var="p" items="${perfiles}">
                <div class="profile-card">
                    <form action="${ctx}/SeleccionPerfil" method="post" class="profile-btn">
                        <input type="hidden" name="accion" value="elegir">
                        <input type="hidden" name="idPerfil" value="${p.idperfil}">
                        <button type="submit"
                                style="background:none;border:none;cursor:pointer;color:inherit;width:100%;padding:0;display:flex;flex-direction:column;align-items:center;gap:var(--space-3);">
                            <div class="profile-avatar">
                                <img src="${ctx}/${p.imagen}" alt="${p.nombre}">
                            </div>
                            <span class="profile-name">${p.nombre}</span>
                        </button>
                    </form>
                </div>
            </c:forEach>

            <c:if test="${puedeAgregarPerfil}">
                <a href="${ctx}/GestionPerfiles" class="profile-card">
                    <div class="profile-btn">
                        <div class="profile-avatar profile-avatar-add" aria-hidden="true">+</div>
                        <span class="profile-name">Agregar perfil</span>
                    </div>
                </a>
            </c:if>
        </section>

        <section class="profiles-actions">
            <a href="${ctx}/GestionPerfiles" class="btn btn-secondary btn-sm">Gestionar perfiles</a>
            <a href="${ctx}/Logout" class="btn btn-ghost btn-sm">Cerrar sesión</a>
        </section>
    </main>

    <!-- ================= MODAL DE RECHAZO ================= -->
    <!-- Aparece cuando el servlet responde con un motivoRechazo.
         Estilo Netflix: te avisa por qué no podés entrar y qué podés hacer. -->
    <c:if test="${not empty motivoRechazo}">
        <div class="modal-rechazo open" role="dialog" aria-modal="true" aria-labelledby="modalRechazoTitle">
            <div class="modal-rechazo-content">
                <div class="modal-rechazo-header">
                    <h3 id="modalRechazoTitle">
                        <c:choose>
                            <c:when test="${motivoRechazo == 'PERFIL_YA_ACTIVO'}">
                                Perfil en uso
                            </c:when>
                            <c:when test="${motivoRechazo == 'PLAN_LLENO'}">
                                Plan lleno
                            </c:when>
                            <c:when test="${motivoRechazo == 'YA_LIBRE'}">
                                Sin cambios
                            </c:when>
                            <c:otherwise>
                                No se puede continuar
                            </c:otherwise>
                        </c:choose>
                    </h3>
                </div>
                <div class="modal-rechazo-body">
                    <p>${mensajeRechazo}</p>

                    <c:if test="${motivoRechazo == 'PLAN_LLENO'}">
                        <div class="cupos-info">
                            Plan <strong>${planNombre}</strong>: hasta
                            <strong>${cuposPlan}</strong> sesiones simultáneas.
                            Para liberar un cupo, cerrá la sesión remota de alguno
                            de los perfiles activos abajo.
                        </div>

                        <c:if test="${not empty perfilesActivosParaCerrar}">
                            <div class="perfiles-para-cerrar">
                                <p style="font-size: var(--fs-micro); color: rgba(255,255,255,0.6); text-transform: uppercase; letter-spacing: 0.08em; margin-bottom: var(--space-2);">
                                    Perfiles activos que podés cerrar
                                </p>
                                <c:forEach var="pa" items="${perfilesActivosParaCerrar}">
                                    <div class="perfil-cerrar-item">
                                        <img src="${ctx}${pa.imagen}" alt="${pa.nombre}">
                                        <span class="nombre">${pa.nombre}</span>
                                        <form action="${ctx}/SeleccionPerfil" method="post" style="margin:0;">
                                            <input type="hidden" name="accion" value="cerrarRemota">
                                            <input type="hidden" name="idPerfil" value="${pa.idperfil}">
                                            <button type="submit" class="btn btn-danger btn-sm"
                                                    onclick="return confirm('¿Cerrar la sesión remota de ${pa.nombre}?\\nSe cerrará la ventana que la tenga abierta.')">
                                                Cerrar
                                            </button>
                                        </form>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:if>
                    </c:if>

                    <c:if test="${motivoRechazo == 'PERFIL_YA_ACTIVO'}">
                        <p style="font-size: var(--fs-micro); color: rgba(255,255,255,0.6);">
                            Si vos o otra persona están usando este perfil en otra ventana,
                            cerrá esa sesión para poder ingresarlo acá.
                        </p>
                        <form action="${ctx}/SeleccionPerfil" method="post">
                            <input type="hidden" name="accion" value="cerrarRemota">
                            <input type="hidden" name="idPerfil" value="${perfilBloqueadoId}">
                            <button type="submit" class="btn btn-danger btn-block"
                                    onclick="return confirm('¿Cerrar la sesión remota de ${perfilBloqueadoNombre}?\\nSe cerrará la ventana que la tenga abierta.')">
                                Cerrar sesión remota de ${perfilBloqueadoNombre}
                            </button>
                        </form>
                    </c:if>
                </div>
                <div class="modal-rechazo-actions">
                    <button type="button" class="btn btn-ghost btn-sm" onclick="cerrarModalRechazo()">
                        Volver
                    </button>
                    <a href="${ctx}/GestionPerfiles" class="btn btn-primary btn-sm">
                        Gestionar perfiles
                    </a>
                </div>
            </div>
        </div>
        <script>
            function cerrarModalRechazo() {
                document.querySelector('.modal-rechazo').classList.remove('open');
            }
            // Cerrar al clickear fuera
            document.querySelector('.modal-rechazo').addEventListener('click', function(e) {
                if (e.target === this) this.classList.remove('open');
            });
            // ESC para cerrar
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') cerrarModalRechazo();
            });
        </script>
    </c:if>

    <!-- ================= MODAL DE ÉXITO ================= -->
    <!-- Aparece cuando se cerró remoto una sesión exitosamente -->
    <c:if test="${not empty motivoExito}">
        <div class="modal-exito open" role="dialog" aria-modal="true" aria-labelledby="modalExitoTitle">
            <div class="modal-exito-content">
                <div class="modal-exito-body">
                    <div class="check-icon" aria-hidden="true">&#10004;</div>
                    <h3 id="modalExitoTitle" style="color: var(--color-surface); margin-bottom: var(--space-3);">
                        Sesión cerrada
                    </h3>
                    <p>${mensajeExito}</p>
                </div>
                <div class="modal-exito-actions">
                    <button type="button" class="btn btn-primary btn-sm" onclick="cerrarModalExito()">
                        Entendido
                    </button>
                </div>
            </div>
        </div>
        <script>
            function cerrarModalExito() {
                document.querySelector('.modal-exito').classList.remove('open');
            }
            document.querySelector('.modal-exito').addEventListener('click', function(e) {
                if (e.target === this) this.classList.remove('open');
            });
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') cerrarModalExito();
            });
        </script>
    </c:if>
</body>
</html>


