<%-- 
    Document   : listarCuentas
    Created on : 2 nov 2026, 15:07:16
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cuentas - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="Web-App/css/base.css">
    <link rel="stylesheet" href="Web-App/css/layout.css">
    <link rel="stylesheet" href="Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="cuentas">
        <section class="section-header">
            <h1>Cuentas registradas</h1>
        </section>

        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Plan</th>
                    <th>Cupos</th>
                    <th>Máx. perfiles</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${cuentas}">
                    <tr>
                        <td>${c.idcuenta}</td>
                        <td>${c.username}</td>
                        <td>${c.plan.nombre}</td>
                        <td>${c.plan.cupos}</td>
                        <td>${c.plan.maxPerfiles}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <section class="alta-cuenta">
            <h2>Alta de cuenta</h2>
            <form action="AgregarCuenta" method="post" class="form-grid">
                <div class="form-group">
                    <label>Username
                        <input type="text" name="username" required>
                    </label>
                </div>
                <div class="form-group">
                    <label>Password
                        <input type="text" name="password" required>
                    </label>
                </div>
                <div class="form-group">
                    <label>Plan
                        <select name="idPlan">
                            <c:forEach var="p" items="${planes}">
                                <option value="${p.idplan}">${p.nombre} (${p.cupos} cupos, ${p.maxPerfiles} perfiles)</option>
                            </c:forEach>
                        </select>
                    </label>
                </div>
                <div class="form-actions form-group-full">
                    <button type="submit" class="btn btn-primary">Crear cuenta</button>
                </div>
            </form>
        </section>
    </main>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>

