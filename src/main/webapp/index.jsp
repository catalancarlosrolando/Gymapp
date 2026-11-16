<%-- 
    Document   : index
    Created on : 2 nov 2026, 14:56:32
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GimnasioApp - Iniciar sesión</title>
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body class="login-page">
    <div class="login-wrapper">
        <div class="login-box">
            <div class="login-logo">
                <span class="icon">&#127947;</span>
                <h1>GimnasioApp</h1>
            </div>
            <p class="login-subtitle">Plataforma de rutinas con control de planes</p>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <c:if test="${not empty mensajeSesionRemota}">
                    <div class="alert alert-success" role="status">
                        Tu sesión fue cerrada desde otra ventana.
           Volvé a ingresar.
                    </div>
            </c:if>

            <form action="Login" method="post">
                <input type="hidden" name="accion" value="entrar">
                <label for="username">Usuario</label>
                <input type="text" id="username" name="username"
                       placeholder="ana, marta, juli..." required autofocus>
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password"
                       placeholder="1234" required>
                <button type="submit" class="btn btn-primary btn-block">Entrar</button>
            </form>

            <div class="login-hint">
                <p><strong>Datos de prueba:</strong></p>
                <ul>
                    <li><code>ana / 1234</code> &mdash; Plan Individual (1 perfil)</li>
                    <li><code>marta / 1234</code> &mdash; Plan Duo (2 perfiles, easter egg)</li>
                    <li><code>juli / 1234</code> &mdash; Plan Familiar (4 perfiles)</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>

