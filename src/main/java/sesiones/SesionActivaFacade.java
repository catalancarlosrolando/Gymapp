package sesiones;

import entidad.Cuenta;
import entidad.Perfil;
import entidad.Plan;
import entidad.SesionActiva;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =====================================================================
 * SesionActivaFacade - CONTROL DE CONCURRENCIA ACID POR PLAN
 * =====================================================================
 *  
 *  |  @ConcurrencyManagement(CONTAINER)
 *  |  @Lock(WRITE)  (anotación EJB)
 *  |  Map en memoria + tabla sesion_activa
 *  |  Sobrevive reinicios (persistencia JTA)
 *  |  Atomicidad post-commit garantizada
 *  |  @AccessTimeout(5 segundos)
 *
 * =====================================================================
 * CÓMO FUNCIONA EL @Lock(WRITE):
 *   El contenedor EJB mantiene un lock exclusivo por bean Singleton.
 *   Cuando un thread invoca un método @Lock(WRITE), el contenedor:
 *     1. Bloquea el bean (otros threads esperan en cola).
 *     2. Ejecuta el método de forma atómica.
 *     3. Libera el lock al terminar.
 *
 *   Thread 1 entra a intentarLogin -> lock adquirido.
 *   Thread 2 llama a intentarLogin -> espera en cola (queue).
 *   Thread 1 termina -> libera lock.
 *   Thread 2 adquiere lock -> lee estado actualizado -> rechazado por cupo.
 *
 * =====================================================================
 * CACHE + BD (FUENTE DE VERDAD):
 *   El Map<Integer, Set<Integer>> en memoria es cache de lectura rápida
 *   para respuestas del navbar. La fuente de verdad es la tabla sesion_activa.
 *   Al iniciar (@PostConstruct), se carga el cache desde la BD.
 *   Al hacer login/logout, se actualizan AMBOS (cache + BD) dentro de la misma
 *   transacción JTA -> si la tx aborta, ambos se revierten -> ACID garantizado.
 * =====================================================================
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)   // <-- CONTAINER, no BEAN
@AccessTimeout(unit = TimeUnit.SECONDS, value = 5)            // evita deadlock indefinido
public class SesionActivaFacade {

    private static final Logger LOG = Logger.getLogger(SesionActivaFacade.class.getName());

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    /**
     * Cache en memoria: idPlan -> Set<idPerfil> activos.
     * Es ConcurrentHashMap porque los métodos @Lock(READ) pueden leer concurrentemente
     * sin pasar por el lock exclusivo del bean.
     */
    private final Map<Integer, Set<Integer>> activosPorPlan = new ConcurrentHashMap<>();

    /**
     * Al iniciar el bean (post-deploy o reinicio de GlassFish), reconstruye el
     * cache desde la tabla sesion_activa. Así las sesiones persistidas sobreviven.
     */
    @PostConstruct
    public void inicializarCache() {
        LOG.info(">>> Inicializando cache de sesiones activas desde BD...");
        try {
            List<SesionActiva> sesiones = em.createNamedQuery("SesionActiva.findAll",
                                                               SesionActiva.class)
                                            .getResultList();
            for (SesionActiva s : sesiones) {
                Integer idPlan = s.getPlan().getIdplan();
                Integer idPerfil = s.getPerfil().getIdperfil();
                activosPorPlan.computeIfAbsent(idPlan,
                        k -> ConcurrentHashMap.newKeySet())
                              .add(idPerfil);
            }
            LOG.log(Level.INFO, ">>> Cache cargado: {0} sesiones activas recuperadas",
                    sesiones.size());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al inicializar cache de sesiones", ex);
        }
    }

    /**
     * Resultado del intento de login. Distingue los 3 casos para que el servlet
     * pueda mostrar el mensaje correcto al usuario.
     */
    public enum ResultadoLogin {
        ACEPTADO,
        PERFIL_YA_ACTIVO,
        PLAN_LLENO
    }

    /**
     * Intenta registrar una sesión activa para el perfil.
     * Anotado @Lock(WRITE) -> garantiza exclusión mutua: un solo thread a la vez.
     * Anotado @TransactionAttribute(REQUIRES_NEW) -> la tx se confirma al finalizar,
     * el INSERT en sesion_activa queda persistido en BD -> ACID post-commit.
     *
     * Reglas (todas se chequean dentro del lock):
     *   1. Si el perfil ya está activo en OTRA sesión -> RECHAZA (PERFIL_YA_ACTIVO).
     *      Un perfil no puede tener dos sesiones HTTP activas a la vez.
     *   2. Si el plan ya tiene sus cupos llenos -> RECHAZA (PLAN_LLENO).
     *   3. Si pasa 1 y 2 -> INSERT en sesion_activa, actualizar cache, ACEPTADO.
     *
     * @return ResultadoLogin.ACEPTADO / PERFIL_YA_ACTIVO / PLAN_LLENO
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ResultadoLogin intentarLogin(Perfil perfil) {
        if (perfil == null || perfil.getCuenta() == null || perfil.getCuenta().getPlan() == null) {
            return ResultadoLogin.PLAN_LLENO; // null-safe: no aceptar
        }
        Integer idPerfil = perfil.getIdperfil();
        Cuenta cuenta = perfil.getCuenta();
        Plan plan = cuenta.getPlan();
        Integer idPlan = plan.getIdplan();
        int cupos = plan.getCupos();

        // 1. Cache rápido (fuente de verdad en memoria)
        Set<Integer> activos = activosPorPlan.computeIfAbsent(idPlan,
                k -> ConcurrentHashMap.newKeySet());

        // Si el perfil ya está activo en OTRA sesión -> RECHAZAR
        // (no se permite reentrada: cada perfil == una sesión HTTP)
        if (activos.contains(idPerfil)) {
            LOG.log(Level.INFO,
                    ">> RECHAZADO PERFIL_YA_ACTIVO: perfil {0} ya tiene sesion activa",
                    idPerfil);
            return ResultadoLogin.PERFIL_YA_ACTIVO;
        }

        // 2. Validar cupo en BD (fuente de verdad persistente)
        Long countBD = em.createNamedQuery("SesionActiva.countByPlan", Long.class)
                         .setParameter("idplan", idPlan)
                         .getSingleResult();
        if (countBD >= cupos) {
            LOG.log(Level.INFO,
                    ">> RECHAZADO PLAN_LLENO: perfil {0} - plan {1} ({2}/{3})",
                    new Object[]{idPerfil, idPlan, countBD, cupos});
            return ResultadoLogin.PLAN_LLENO;
        }

        // 3. INSERT en sesion_activa (dentro de tx JTA -> ACID)
        SesionActiva sa = new SesionActiva();
        sa.setPerfil(perfil);
        sa.setCuenta(cuenta);
        sa.setPlan(plan);
        sa.setFechaInicio(new Date());
        em.persist(sa);

        // 4. Actualizar cache
        activos.add(idPerfil);

        LOG.log(Level.INFO,
                ">> LOGIN OK: perfil {0} - plan {1} ({2}/{3})",
                new Object[]{idPerfil, idPlan, countBD + 1, cupos});
        return ResultadoLogin.ACEPTADO;
    }

    /**
     * Libera el cupo del plan cuando el perfil cierra sesión.
     * @Lock(WRITE) para atomicidad con intentarLogin.
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logout(Perfil perfil) {
        if (perfil == null) return;
        Integer idPerfil = perfil.getIdperfil();
        Integer idPlan = (perfil.getCuenta() != null && perfil.getCuenta().getPlan() != null)
                       ? perfil.getCuenta().getPlan().getIdplan()
                       : null;

        // DELETE de la BD
        Query q = em.createNamedQuery("SesionActiva.deleteByPerfil");
        q.setParameter("idperfil", idPerfil);
        q.executeUpdate();

        // Actualizar cache
        if (idPlan != null) {
            Set<Integer> activos = activosPorPlan.get(idPlan);
            if (activos != null) {
                activos.remove(idPerfil);
            }
        }
        LOG.log(Level.INFO, ">> LOGOUT: perfil {0} liberado", idPerfil);
    }

    /**
     * Lectura del cache: NO necesita lock exclusivo.
     * @Lock(READ) permite acceso concurrente de muchos threads.
     */
    @Lock(LockType.READ)
    public int sesionesActivasDe(Integer idPlan) {
        Set<Integer> activos = activosPorPlan.get(idPlan);
        return activos == null ? 0 : activos.size();
    }

    /**
     * Verifica si un perfil específico sigue con sesión activa.
     * Lo usa el FiltroSesionActiva para detectar ventanas huérfanas
     * (sesiones HTTP cuyo perfil fue cerrado remotamente desde otra ventana).
     *
     * @Lock(READ) -> multiple threads pueden leer concurrentemente.
     */
    @Lock(LockType.READ)
    public boolean sigueActivo(Perfil perfil) {
        if (perfil == null) return false;
        Integer idPerfil = perfil.getIdperfil();
        Integer idPlan = (perfil.getCuenta() != null && perfil.getCuenta().getPlan() != null)
                       ? perfil.getCuenta().getPlan().getIdplan()
                       : null;
        if (idPlan == null) return false;
        Set<Integer> activos = activosPorPlan.get(idPlan);
        return activos != null && activos.contains(idPerfil);
    }

    /**
     * Devuelve los perfiles activos de una cuenta (para mostrar "quién está
     * entrenando" en el selector). Solo lectura -> @Lock(READ).
     */
    @Lock(LockType.READ)
    public List<Integer> perfilesActivosDeCuenta(Integer idCuenta) {
        return em.createQuery(
                 "SELECT s.perfil.idperfil FROM SesionActiva s WHERE s.cuenta.idcuenta = :id",
                 Integer.class)
                 .setParameter("id", idCuenta)
                 .getResultList();
    }

    /**
     * Limpia sesiones huérfanas (un perfil marcado activo en BD pero cuyo
     * HttpSession expiró). Lo llama un @Schedule o el arranque.
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int limpiarSesionesHuerfanas() {
        int n = em.createQuery("DELETE FROM SesionActiva s WHERE s.fechaInicio < :hace24h")
                  .setParameter("hace24h",
                                new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000))
                  .executeUpdate();
        if (n > 0) {
            LOG.log(Level.INFO, ">> Limpieza: {0} sesiones huérfanas eliminadas", n);
            // Rebuild cache
            activosPorPlan.clear();
            inicializarCache();
        }
        return n;
    }
}