<%-- 
    Document   : perfil
    Created on : 2 nov 2026, 15:07:52
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi perfil - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="perfil">
        <h1>Mi perfil</h1>

        <section class="perfil-header">
            <img src="${perfil.imagen}" alt="${perfil.nombre}" class="perfil-avatar">
            <div>
                <h2>${perfil.nombre}</h2>
                <p class="muted">Pertenece a la cuenta <strong>${cuentaLogueada.username}</strong>
                   &middot; Plan <strong>${cuentaLogueada.plan.nombre}</strong></p>
            </div>
        </section>

        <form action="ModificarPerfil" method="post" class="form-grid">
            <div class="form-group form-group-full">
                <label for="nombre">Nombre del perfil</label>
                <input type="text" id="nombre" name="nombre" value="${perfil.nombre}" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${perfil.email}">
            </div>
            <div class="form-group">
                <label for="fechanacimiento">Fecha de nacimiento</label>
                <input type="date" id="fechanacimiento" name="fechanacimiento"
                       value="${perfil.fechanacimiento == null ? '' :
                              perfil.fechanacimiento.time}">
            </div>
            <div class="form-group">
                <label for="genero">Género</label>
                <select id="genero" name="genero">
                    <option value="masculino" <c:if test="${perfil.genero == 'masculino'}">selected</c:if>>Masculino</option>
                    <option value="femenino"  <c:if test="${perfil.genero == 'femenino'}">selected</c:if>>Femenino</option>
                    <option value="otro"      <c:if test="${perfil.genero == 'otro'}">selected</c:if>>Otro</option>
                </select>
            </div>
            <div class="form-group">
                <label for="tema">Tema</label>
                <select id="tema" name="tema">
                    <option value="claro"  <c:if test="${perfil.tema == 'claro'}">selected</c:if>>Claro</option>
                    <option value="oscuro" <c:if test="${perfil.tema == 'oscuro'}">selected</c:if>>Oscuro</option>
                </select>
            </div>
            <div class="form-group">
                <label for="imagen">Imagen (URL)</label>
                <input type="text" id="imagen" name="imagen" value="${perfil.imagen}">
            </div>

            <div class="form-actions form-group-full">
                <a href="Home" class="btn btn-ghost">Cancelar</a>
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
            </div>
        </form>

        <section class="perfil-info-extra">
            <h3>Acciones rápidas</h3>
            <a href="CambiarPerfil" class="btn btn-secondary">Cambiar de perfil</a>
            <a href="GestionPerfiles" class="btn btn-secondary">Gestionar perfiles de la cuenta</a>
            <a href="LogoutPerfil" class="btn btn-danger"
               onclick="return confirm('¿Cerrar sesión de este perfil?')">
               Cerrar sesión de ${perfil.nombre}
            </a>
        </section>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>
