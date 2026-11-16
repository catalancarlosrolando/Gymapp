<%-- 
    Document   : crearRutina
    Created on : 2 nov 2026, 15:06:04
    Author     : carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear rutina - GimnasioApp</title>
    <link rel="icon" type="image/png" href="Web-App/img/logo.png">
    <link rel="stylesheet" href="${ctx}/Web-App/css/base.css">
    <link rel="stylesheet" href="${ctx}/Web-App/css/layout.css">
    <link rel="stylesheet" href="${ctx}/Web-App/css/pages.css">
</head>
<body>
    <%@include file="/WEB-INF/vista/componentes/header.jsp" %>

    <main class="crear-rutina">
        <h1>Crear rutina para ${perfilActivo.nombre}</h1>

        <form action="${ctx}/CrearRutina" method="post" id="formRutina">
            <div class="form-grid">
                <div class="form-group">
                    <label for="nombre">Nombre *</label>
                    <input type="text" id="nombre" name="nombre" required>
                </div>
                <div class="form-group">
                    <label for="categoria">Categoría</label>
                    <select id="categoria" name="categoria">
                        <option value="push">Push (empuje)</option>
                        <option value="pull">Pull (tiraje)</option>
                        <option value="legs">Legs (piernas)</option>
                        <option value="core">Core (abdomen)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="imagen">Imagen (URL)</label>
                    <input type="text" id="imagen" name="imagen" placeholder="Web-App/img/r-default.png">
                </div>
                <div class="form-group">
                    <label for="tiempo">Tiempo total (segundos)</label>
                    <input type="number" id="tiempo" name="tiempo" min="0">
                </div>
                <div class="form-group form-group-full">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" name="descripcion" rows="3"></textarea>
                </div>
            </div>

            <h2>Ejercicios de la rutina</h2>
            <div id="ejerciciosAgregados" class="ejercicios-agregados">
                <!-- Aquí se inyectan las cards de ejercicios agregados vía JS -->
            </div>

            <button type="button" class="btn btn-secondary" onclick="abrirModal()">
                + Agregar ejercicio
            </button>

            <div class="form-actions">
                <a href="${ctx}/Rutinas" class="btn btn-ghost">Cancelar</a>
                <button type="submit" class="btn btn-primary">Crear rutina</button>
            </div>
        </form>
    </main>

    <!-- Modal de catálogo de ejercicios  ${ctx} -->
    <div id="modalEjercicios" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Catálogo de ejercicios</h3>
                <button type="button" class="modal-close" onclick="cerrarModal()">&times;</button>
            </div>
            <div class="modal-body">
                <c:forEach var="e" items="${ejercicios}">
                    <div class="ejercicio-catalogo-card">
                        <img src="${e.imagen}" alt="${e.nombre}">
                        <div>
                            <h4>${e.nombre}</h4>
                            <p class="muted">Series: ${e.series} &middot; Repes: ${e.repeticiones} &middot; Peso: ${e.peso} kg</p>
                            <p class="muted small">${e.descripción}</p>
                        </div>
                        <button type="button" class="btn btn-secondary btn-sm"
                                onclick="agregarEjercicio(${e.idejercicio}, '${e.nombre}')">
                            + Agregar
                        </button>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <script>
        function abrirModal() {
            document.getElementById('modalEjercicios').style.display = 'flex';
        }
        function cerrarModal() {
            document.getElementById('modalEjercicios').style.display = 'none';
        }
        function agregarEjercicio(id, nombre) {
            const cont = document.getElementById('ejerciciosAgregados');
            const div = document.createElement('div');
            div.className = 'ejercicio-agregado';
            div.id = 'ej-' + id;
            div.innerHTML = `
                <h4>\${nombre}</h4>
                <div class="ejercicio-fields">
                    <label>Series <input type="number" name="ejercicio_\${id}_series" value="4" min="1"></label>
                    <label>Repes <input type="number" name="ejercicio_\${id}_repeticiones" value="10" min="1"></label>
                    <label>Peso <input type="number" step="0.01" name="ejercicio_\${id}_peso" value="0"></label>
                    <label>Tiempo <input type="number" name="ejercicio_\${id}_tiempo" value="60"></label>
                    <label>Descanso <input type="number" name="ejercicio_\${id}_descanso" value="90"></label>
                    <button type="button" class="btn btn-danger btn-sm" onclick="this.parentElement.parentElement.remove()">Quitar</button>
                </div>`;
            cont.appendChild(div);
        }
        document.getElementById('modalEjercicios').addEventListener('click', function(e) {
            if (e.target === this) cerrarModal();
        });
    </script>

    <%@include file="/WEB-INF/vista/componentes/footer.jsp" %>
</body>
</html>
