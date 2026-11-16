<%-- 
    Document   : gestionarPerfiles
    Created on : 2 nov 2026, 15:06:30
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestionar perfiles - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body class="profiles-page">
    <header class="navbar navbar-light">
        <div class="brand"><a href="Home">GimnasioApp</a></div>
        <nav>
            <a href="Home">Inicio</a>
            <a href="SeleccionPerfil">Volver al selector</a>
            <a href="Logout">Cerrar sesión</a>
        </nav>
    </header>

    <main class="gestion-perfiles">
        <section class="gestion-header">
            <h1>Gestionar perfiles</h1>
            <p>Cuenta: <strong>${cuentaLogueada.username}</strong>
               &middot; Plan <strong>${cuentaLogueada.plan.nombre}</strong>
               &middot; ${countPerfiles}/${maxPerfiles} perfiles usados</p>
        </section>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty info}">
            <div class="alert alert-success">${info}</div>
        </c:if>

        <section class="perfiles-grid">
            <c:forEach var="p" items="${perfiles}">
                <div class="perfil-card-edit">
                    <img src="${p.imagen}" alt="${p.nombre}" class="perfil-edit-avatar">
                    <form action="GestionPerfiles" method="post" class="perfil-edit-form">
                        <input type="hidden" name="accion" value="editar">
                        <input type="hidden" name="idPerfil" value="${p.idperfil}">
                        <label>Nombre
                            <input type="text" name="nombre" value="${p.nombre}" required>
                        </label>
                        <label>Email
                            <input type="email" name="email" value="${p.email}">
                        </label>
                        <label>Género
                            <select name="genero">
                                <option value="masculino" <c:if test="${p.genero == 'masculino'}">selected</c:if>>Masculino</option>
                                <option value="femenino"  <c:if test="${p.genero == 'femenino'}">selected</c:if>>Femenino</option>
                                <option value="otro"      <c:if test="${p.genero == 'otro'}">selected</c:if>>Otro</option>
                            </select>
                        </label>
                        <label>Tema
                            <select name="tema">
                                <option value="claro"  <c:if test="${p.tema == 'claro'}">selected</c:if>>Claro</option>
                                <option value="oscuro" <c:if test="${p.tema == 'oscuro'}">selected</c:if>>Oscuro</option>
                            </select>
                        </label>
                        <label>Imagen (URL)
                            <input type="text" name="imagen" value="${p.imagen}">
                        </label>
                        <div class="perfil-edit-actions">
                            <button type="submit" class="btn btn-primary btn-sm">Guardar</button>
                            <a href="SeleccionPerfil" class="btn btn-secondary btn-sm">Probar</a>
                            <c:if test="${countPerfiles > 1}">
                                <button type="submit" formaction="GestionPerfiles?accion=eliminar"
                                        name="idPerfil" value="${p.idperfil}"
                                        class="btn btn-danger btn-sm"
                                        onclick="return confirm('¿Eliminar el perfil ${p.nombre}?')">
                                    Eliminar
                                </button>
                            </c:if>
                        </div>
                    </form>
                </div>
            </c:forEach>
        </section>

        <c:if test="${puedeAgregar}">
            <section class="agregar-perfil">
                <h2>Agregar nuevo perfil</h2>
                <p class="muted">Te quedan <strong>${maxPerfiles - countPerfiles}</strong>
                   perfil(es) disponible(s) según tu plan.</p>
                <form action="GestionPerfiles" method="post" class="form-grid">
                    <input type="hidden" name="accion" value="agregar">
                    <div class="form-group">
                        <label for="nuevo-nombre">Nombre</label>
                        <input type="text" id="nuevo-nombre" name="nombre" required>
                    </div>
                    <div class="form-group">
                        <label for="nuevo-email">Email</label>
                        <input type="email" id="nuevo-email" name="email">
                    </div>
                    <div class="form-group">
                        <label for="nuevo-genero">Género</label>
                        <select id="nuevo-genero" name="genero">
                            <option value="masculino">Masculino</option>
                            <option value="femenino">Femenino</option>
                            <option value="otro">Otro</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="nuevo-tema">Tema</label>
                        <select id="nuevo-tema" name="tema">
                            <option value="claro">Claro</option>
                            <option value="oscuro">Oscuro</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="nuevo-imagen">Imagen (URL)</label>
                        <input type="text" id="nuevo-imagen" name="imagen"
                               placeholder="/Web-App/img/avatar-default.png">
                    </div>
                    <div class="form-actions form-group-full">
                        <button type="submit" class="btn btn-primary">Crear perfil</button>
                    </div>
                </form>
            </section>
        </c:if>

        <c:if test="${!puedeAgregar}">
            <p class="info-box">Tu plan <strong>${cuentaLogueada.plan.nombre}</strong>
               permite hasta <strong>${maxPerfiles}</strong> perfiles. Eliminá uno
               para poder crear otro, o upgradeá de plan.</p>
        </c:if>
    </main>
</body>
</html>
