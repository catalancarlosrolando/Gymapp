# GimnasioApp — Sistema de Gestión de Rutinas con Control de Planes

Proyecto web Java Enterprise que adapta el **modelo de planes de Netflix** a un gimnasio: cada plan define cuántos perfiles puede tener una cuenta y cuántas sesiones activas simultáneas admite. Incluye control de concurrencia **ACID** real con `@Lock(WRITE)` + persistencia en BD, selector de perfil estilo Netflix, y UI/UX basada en el design system de Nike Training Club (NTC).

**Autor:** Carlos Catalan   
**Stack:** Jakarta EE 11 · GlassFish 8 · MySQL 8 · NetBeans 31 · Maven.

---

## Tabla de contenidos

1. [Mini mundo y reglas de negocio](#1-mini-mundo-y-reglas-de-negocio)
2. [Diseño de la base de datos](#2-diseño-de-la-base-de-datos)
3. [Tecnologías utilizadas](#3-tecnologías-utilizadas)
4. [Arquitectura del proyecto](#4-arquitectura-del-proyecto)
5. [Funcionalidades](#5-funcionalidades)
6. [Instalación y ejecución](#6-instalación-y-ejecución)
7. [Calidades del proyecto](#7-calidades-del-proyecto)

---

## 1. Mini mundo y reglas de negocio

### Mini mundo

Un gimnasio quiere ofrecer planes al estilo Netflix: una **cuenta** paga un plan (Individual, Duo o Familiar) y dentro de esa cuenta pueden existir varios **perfiles** (uno por persona que entrena). Al ingresar, el usuario elige qué perfil quiere usar en esa sesión. Cada plan define dos números:

- **`cupos`**: cuántas sesiones activas simultáneas permite el plan.
- **`max_perfiles`**: cuántos perfiles puede crear la cuenta.

Cada perfil tiene sus propias **rutinas**, y cada rutina se arma con **ejercicios** del catálogo, seteando para cada uno atributos personalizados (series, repeticiones, peso, tiempo, descanso). El sistema debe garantizar que un plan nunca supere su cupo de sesiones activas, incluso si dos usuarios intentan loguearse exactamente al mismo tiempo.

### Reglas de negocio

1. Una cuenta pertenece a **un único plan** y no puede cambiar de plan.
2. Un plan define `cupos` (sesiones activas simultáneas) y `max_perfiles` (perfiles creables).
3. Una cuenta puede tener entre 1 y `max_perfiles` perfiles; nunca más.
4. Un perfil pertenece a una sola cuenta.
5. Un perfil puede tener **una sola sesión activa** a la vez (no se permite reentrada desde otra ventana).
6. El total de sesiones activas de los perfiles de un mismo plan no puede superar `cupos`.
7. Una rutina pertenece a un perfil.
8. Una rutina tiene N ejercicios; cada uno con atributos propios (series, repes, peso, etc.).
9. Un ejercicio del catálogo puede aparecer en muchas rutinas con atributos distintos.
10. Si una cuenta cierra sesión remota de un perfil, la otra ventana que tenía ese perfil activo debe ser expulsada al login.

---

## 2. Diseño de la base de datos

### Entidades y atributos

| Tabla | PK | FKs | Atributos | Propósito |
| --- | --- | --- | --- | --- |
| `plan` | `idplan` | — | `nombre VARCHAR(50)`, `cupos INT`, `max_perfiles INT` | Catálogo de planes (1, 2 o 4 cupos) |
| `cuenta` | `idcuenta` | `plan_idplan → plan` | `username VARCHAR(50) UNIQUE`, `password VARCHAR(100)` | Credenciales + plan contratado |
| `perfil` | `idperfil` | `cuenta_idcuenta → cuenta` | `nombre VARCHAR(100)`, `email VARCHAR(100)`, `fechanacimiento DATE`, `genero VARCHAR(20)`, `tema VARCHAR(10)`, `imagen VARCHAR(255)` | Perfiles de la cuenta (N-1) |
| `rutina` | `idrutina` | `perfil_idperfil → perfil` | `nombre VARCHAR(100)`, `categoría VARCHAR(20)`, `imagen VARCHAR(255)`, `tiempo INT`, `descripción TEXT`, `realizada TINYINT(1)` | Rutinas del perfil |
| `ejercicio` | `idejercicio` | — | `nombre VARCHAR(100)`, `imagen VARCHAR(255)`, `tiempo INT`, `series INT`, `repeticiones INT`, `peso DECIMAL(6,2)`, `descanso INT`, `descripción TEXT` | Catálogo de ejercicios |
| `rutina_has_ejercicio` | (`rutina_idrutina`, `ejercicio_idejercicio`) | ambas FK | `series INT`, `repeticiones INT`, `peso DECIMAL(6,2)`, `tiempo INT`, `descanso INT` | N-M con atributos propios |
| `sesion_activa` | `idsesion` | `perfil_idperfil` (UNIQUE), `cuenta_idcuenta`, `plan_idplan` | `fecha_inicio DATETIME`, `http_session_id VARCHAR(100)` | Persistencia ACID de sesiones activas |

### Relaciones y cardinalidades

- `plan` **1 : N** `cuenta`
- `cuenta` **1 : N** `perfil`
- `perfil` **1 : N** `rutina`
- `rutina` **N : M** `ejercicio` (materializada en `rutina_has_ejercicio` con atributos)
- `perfil` **1 : 1** `sesion_activa` (UNIQUE sobre `perfil_idperfil`)

### Diseño conceptual (modelo E-R)

```
  ┌────────┐ 1     N ┌────────┐ 1     N ┌────────┐ 1     N ┌─────────┐
  │  plan  │─────────│ cuenta │─────────│ perfil │─────────│ rutina  │
  └────────┘         └────────┘         └────────┘         └─────────┘
                          │                  │                  │ M         ┌──────────────────────┐
                          │                  │                  │           │ rutina_has_ejercicio │
                          │                  │                  │───────────│   (atributos N-M)    │
                          │                  │                  │           └──────────────────────┘
                          │ 1                │ 1                │
                          │                  │                  │ N
                          ▼                  ▼                  ▼
                     ┌────────────┐     ┌──────────────┐  ┌──────────────┐
                     │   (null)   │     │ sesion_activa│  │  ejercicio   │
                     └────────────┘     └──────────────┘  └──────────────┘
                                                             
```

### Modelo lógico / físico

- **Normalización:** 3FN. La tabla intermedia `rutina_has_ejercicio` resuelve la N-M con clave compuesta y atributos propios (no se puede mover a `ejercicio` porque los atributos son de la relación, no del catálogo).
- **Charset:** `utf8mb4` con collation `utf8mb4_0900_ai_ci` (necesario para acentos y eñes en `push`, `pull`, `descripción`).
- **Storage Engine:** `InnoDB` (para respetar FKs y constraints).
- **Restricciones:**
  - `UNIQUE` sobre `cuenta.username` (no dos cuentas con mismo username).
  - `UNIQUE` sobre `perfil.perfil_idperfil` en `sesion_activa` (un perfil no puede tener dos sesiones activas a nivel BD).
  - `ON DELETE CASCADE` en FKs de `perfil`, `rutina`, `rutina_has_ejercicio`, `sesion_activa` (borrar cuenta borra en cascada).
  - `ON DELETE RESTRICT` en `plan` (no se puede borrar un plan con cuentas asociadas).

### Motor de base de datos

**MySQL 8.x** (verificado con 8.4.11). Script SQL completo en `bdgimnasio_v2.sql`.

---

## 3. Tecnologías utilizadas

| Componente | Versión | Verificación |
| --- | --- | --- |
| JDK | OpenJDK 17+ (verificado con 21) | `java -version` |
| Jakarta EE API | 11.0.0-M1 (scope provided) | — |
| Maven | 3.8+ | `mvn -version` |
| GlassFish | 8.0.0 | `asadmin version` |
| MySQL | 8.x | `mysql --version` |
| Connector/J | 26.7.0 (bundled en WEB-INF/lib) | — |
| JSTL | 3.0.x (`jakarta.tags.core`) | — |
| EclipseLink | 5.0.0-B13 (incluido en GlassFish 8) | — |
| NetBeans | 31 | Help → About |
| Namespace | `jakarta.*` (NO `javax.*`) | — |

---

## 4. Arquitectura del proyecto

### Patrón

**MVC + Session Facade + Singleton EJB**:

- **Modelo (entidad):** clases JPA anotadas con `@Entity` en el paquete `entidad`.
- **Servicios (sesiones):** EJB `@Stateless` que extienden `AbstractFacade<T>` en el paquete `sesiones`. El contenedor maneja EM y transacciones JTA automáticamente.
- **Controlador:** servlets en el paquete `controlador` con `@WebServlet` y routing interno por `request.getServletPath()`.
- **Vista:** JSP con JSTL (`jakarta.tags.core`) bajo `WEB-INF/vista/`, accesibles solo vía forward del servlet.
- **Concurrencia:** EJB `@Singleton` con `@ConcurrencyManagement(CONTAINER)` y `@Lock(WRITE)` para exclusión mutua atómica.
- **Filtro:** `FiltroSesionActiva` detecta ventanas huérfanas.

### Estructura de paquetes

```
GimnasioApp/
├── pom.xml
├── bdgimnasio_v2.sql
├── src/main/java/
│   ├── entidad/        8 clases @Entity (Plan, Cuenta, Perfil, Rutina, Ejercicio, RutinaHasEjercicio, RutinaHasEjercicioPK, SesionActiva)
│   ├── sesiones/       8 EJB (AbstractFacade + 6 concretos + SesionActivaFacade @Singleton con @Lock(WRITE))
│   └── controlador/    10 servlets (Login, SeleccionPerfil, LogoutPerfil, Home, Rutina, Ejercicio, Perfil, GestionPerfiles, Cuenta, FiltroSesionActiva)
├── src/main/resources/META-INF/persistence.xml
└── src/main/webapp/
    ├── index.jsp        Login con hero a sangre
    ├── Web-App/css/     base.css + layout.css + pages.css (tokens NTC)
    ├── Web-App/img/     26 imágenes PNG (logo, hero, avatares, ejercicios, rutinas)
    └── WEB-INF/vista/   11 JSP internas + componentes/header.jsp + componentes/footer.jsp
```

### Decisiones de diseño

- **JTA (no RESOURCE_LOCAL):** las transacciones las maneja el contenedor GlassFish vía JTA. El código de aplicación **nunca** llama a `em.getTransaction().begin()`.
- **`@PersistenceContext` en EJB:** el EntityManager lo inyecta el contenedor. **No** usar `@PersistenceUnit` ni `UserTransaction` (mezclar las dos versiones compite y da errores oscuros).
- **Driver MySQL bundled:** el `mysql-connector-j` está en `WEB-INF/lib` sin `scope=provided`. **No** copiar a `domain1/lib`.
- **JSP bajo `WEB-INF/vista`:** no accesibles por URL directa. Solo vía forward del servlet.
- **Namespace `jakarta.*`:** obligatorio en GlassFish 8 (NO `javax.*`).
- **JSTL URI `jakarta.tags.core`:** las URIs viejas `http://java.sun.com/...` no resuelven en Jakarta EE 9+.

---
                                            
## 5. Funcionalidades

### Autenticación y perfiles

- Login con credenciales (username + password).
- Selector de perfil intermedio estilo Netflix ("¿Quién entrena hoy?") con cards en B/N.
- Perfiles bloqueados visualmente (candado 🔒) cuando ya tienen sesión activa.
- Botón "Cerrar sesión remota" para liberar un perfil activo en otra ventana.
- Cambio de perfil sin cerrar sesión de cuenta (`/CambiarPerfil`).
- Gestión de perfiles (alta/edición/baja) respetando `max_perfiles` del plan.

### Control de concurrencia ACID

- `@Singleton` + `@Lock(WRITE)` + `@TransactionAttribute(REQUIRES_NEW)` en `SesionActivaFacade.intentarLogin`.
- `enum ResultadoLogin { ACEPTADO, PERFIL_YA_ACTIVO, PLAN_LLENO }` para mensajes diferenciados.
- Tabla `sesion_activa` persistente: las sesiones **sobreviven reinicios de GlassFish**.
- `@PostConstruct inicializarCache()`: al arrancar, reconstruye el cache en memoria desde la BD.
- `FiltroSesionActiva`: invalida HttpSession de ventanas huérfanas y redirige al login con mensaje.
- `navigator.sendBeacon`: detecta cierre de pestaña y avisa al servidor para liberar el cupo.

### Rutinas y ejercicios

- CRUD completo de rutinas (crear, ver, completar, eliminar).
- Modal con catálogo de ejercicios para agregar a una rutina.
- Cada ejercicio agregado setea series, repes, peso, tiempo y descanso propios.
- Marcar rutina como realizada (desaparece de pendientes).
- Cards con imagen, nombre, descripción y stats por ejercicio.

### UI/UX (basada en Nike Training Club)

- Design tokens: monocromo (`#111111`, `#FFFFFF`, `#F5F5F5`), tipografía Helvetica Now, escala 4px.
- Botones píldora (radius 30px), área de toque mínima 44px (WCAG 2.2 AA).
- Hero a sangre en login, navbar sticky con backdrop blur, footer 4 columnas.
- 26 imágenes generadas en estilo B/N deportivo (logo, hero, 10 avatares, 10 ejercicios, 4 rutinas).
- Mobile-first responsive con breakpoints 600/960/1280/1440.

---

## 6. Instalación y ejecución

### Requisitos previos

- JDK 17 o superior instalado.
- MySQL 8.x corriendo en `localhost:3306`.
- GlassFish 8.0.0 instalado (ej. en `/home/carlos/GlassFish_Server`).
- NetBeans 31 (opcional pero recomendado).

### Paso 1: crear la base de datos

Abrí MySQL Workbench (o tu cliente preferido) y ejecutá el script `bdgimnasio_v2.sql`:

```bash
mysql -u root -p < bdgimnasio_v2.sql
```

Verificá que se cargaron los datos semilla:

```sql
SHOW TABLES;                       -- debe listar 7 tablas
SELECT COUNT(*) FROM plan;         -- 3
SELECT COUNT(*) FROM ejercicio;    -- 10
SELECT COUNT(*) FROM cuenta;       -- 3
SELECT COUNT(*) FROM perfil;       -- 6
```

### Paso 2: crear el pool JDBC y el recurso JNDI en GlassFish

**Importante:** NO uses el wizard de NetBeans para crear el JDBC Resource porque lo registra con scope `java:app/` y rompe el deploy. Hacelo global con `asadmin`:

```bash
# Path al asadmin (ajustá según tu instalación)
ASADMIN=/home/carlos/GlassFish_Server/glassfish/bin/asadmin

# Iniciar dominio
$ASADMIN start-domain domain1

# Crear pool de conexiones (reemplazá __PWD__ por tu password de MySQL)
$ASADMIN create-jdbc-connection-pool \
    --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
    --restype javax.sql.DataSource \
    --property "User=root:Password=__PWD__:URL=jdbc:mysql://localhost:3306/bdgimnasio:ServerName=localhost:PortNumber=3306:DatabaseName=bdgimnasio:UseSSL=false:AllowPublicKeyRetrieval=true" \
    bdgimnasioPool

# Probar el pool
$ASADMIN ping-connection-pool bdgimnasioPool
# debe decir: Command ping-connection-pool executed successfully

# Crear JDBC Resource con JNDI global (sin java:app/)
$ASADMIN create-jdbc-resource \
    --connectionpoolid bdgimnasioPool \
    jdbc/bdgimnasio
```

### Paso 3: configurar el proyecto

1. Abrí NetBeans → **File → Open Project** → seleccioná la carpeta `GimnasioApp`.
2. Verificá que el archivo `src/main/webapp/WEB-INF/glassfish-resources.xml` **NO** exista (lo eliminamos para evitar el bug del wizard que registra con `java:app/`).
3. Verificá el `pom.xml` tenga el `maven-war-plugin` con `<nonFilteredFileExtensions>` para que las imágenes PNG no se filtren.

### Paso 4: build y deploy

```bash
# Desde NetBeans: click derecho sobre el proyecto → Clean and Build (F11)
# O desde terminal con Maven:
cd /ruta/al/proyecto/GimnasioApp
mvn clean package

# Deploy a GlassFish
$ASADMIN deploy target/GimnasioApp-1.0-SNAPSHOT.war
```

### Paso 5: verificar

Abrí en el navegador: `http://localhost:8080/GimnasioApp/`

Datos de prueba:

| Username | Password | Plan | Perfiles |
| --- | --- | --- | --- |
| `ana` | `1234` | Individual (1 cupo) | 1 perfil |
| `marta` | `1234` | Duo (2 cupos) | 2 perfiles (easter egg) |
| `juli` | `1234` | Familiar (4 cupos) | 3 perfiles |

### Casos de prueba de concurrencia

1. Login `marta/1234` → elegir "Marta Entrena" → entra (1/2).
2. Abrir ventana incógnita → login `marta/1234` → elegir "Marta Pareja" → entra (2/2).
3. Tercer intento → rechazado: "Tu plan Duo ya tiene el máximo de perfiles activos (2)".
4. Cerrar la pestaña del paso 1 → en el log aparece `>> LOGOUT: perfil X liberado`.
5. Desde la ventana incógnita, recargar → "Marta Entrena" ahora aparece libre.

---

## 7. Cualidades del proyecto

- **ACID real garantizado:** `@Lock(WRITE)` + `@TransactionAttribute(REQUIRES_NEW)` + `@AccessTimeout(5s)` en `SesionActivaFacade`. Atomicidad post-commit, no solo en memoria.
- **Persistencia de sesiones:** la tabla `sesion_activa` sobrevive reinicios de GlassFish. El `@PostConstruct` reconstruye el cache desde la BD.
- **Cache + BD (doble fuente de verdad):** el `Map<Integer, Set<Integer>>` en memoria es cache de lectura rápida; la BD es la fuente de verdad transaccional.
- **Mensajes diferenciados:** `enum ResultadoLogin` distingue `PERFIL_YA_ACTIVO` de `PLAN_LLENO` para UX clara.
- **Filtro de ventanas huérfanas:** `FiltroSesionActiva` invalida HttpSession si el perfil fue cerrado remotamente desde otra ventana.
- **Detección de cierre de pestaña:** `navigator.sendBeacon` + `pagehide` libera el cupo automáticamente.
- **UI/UX profesional:** design system basado en Nike Training Club con tokens formales, accesibilidad WCAG 2.2 AA, mobile-first.
- **Separación cuenta vs perfil:** modelo Netflix real que permite el easter egg del plan Duo (2 perfiles intercambiables).
- **JOIN FETCH + hints de cache:** queries optimizadas para evitar N+1 selects y el bug de L2 cache de EclipseLink.
- **Sin invención de datos:** todas las decisiones están justificadas en el plan de acción original o en el refactor documentado.

---

### Agradecimientos

- Design system inspirado en **Nike Training Club (NTC)** — minimalismo monocromo, tipografía contundente, botones píldora.
- Patrones: **MVC + Session Facade + Singleton EJB** según especificación Jakarta EE 11.    
