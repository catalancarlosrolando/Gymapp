package sesiones;

import entidad.RutinaHasEjercicio;
import entidad.RutinaHasEjercicioPK;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RutinaHasEjercicioFacade extends AbstractFacade<RutinaHasEjercicio> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public RutinaHasEjercicioFacade() { super(RutinaHasEjercicio.class); }

    /**
     * Todos los ejercicios (con atributos seteados) de una rutina.
     *
     * IMPORTANTE: usa LEFT JOIN FETCH con r.ejercicio para garantizar que
     * la relación EAGER se cargue en el mismo SELECT. Sin el JOIN FETCH,
     * aunque la entidad tenga @ManyToOne(fetch=EAGER), EclipseLink puede
     * no cargar la relación al usar createQuery() (sí la carga con em.find()
     * pero NO con queries JPQL personalizadas).
     *
     * Esto hace que ${d.ejercicio.nombre} y ${d.ejercicio.imagen} funcionen
     * en la JSP sin LazyInitializationException ni valores null.
     */
    public List<RutinaHasEjercicio> findByRutina(Integer idRutina) {
        return em.createQuery(
                 "SELECT r FROM RutinaHasEjercicio r "
               + "LEFT JOIN FETCH r.ejercicio "
               + "LEFT JOIN FETCH r.rutina "
               + "WHERE r.rutina.idrutina = :idrutina "
               + "ORDER BY r.rutinaHasEjercicioPK.ejercicioIdejercicio",
                 RutinaHasEjercicio.class)
                 .setParameter("idrutina", idRutina)
                 .setHint("jakarta.persistence.cacheRetrieveMode", "REFRESH")
                 .setHint("jakarta.persistence.cacheStoreMode", "REFRESH")
                 .getResultList();
    }

    /** Elimina todas las asociaciones de una rutina (para edición). */
    public void deleteByRutina(Integer idRutina) {
        em.createQuery("DELETE FROM RutinaHasEjercicio r WHERE r.rutina.idrutina = :id")
          .setParameter("id", idRutina)
          .executeUpdate();
    }

    /** Crea una asociación nueva con sus atributos. */
    public void agregarEjercicio(Integer idRutina, Integer idEjercicio,
                                 Integer series, Integer repeticiones,
                                 java.math.BigDecimal peso,
                                 Integer tiempo, Integer descanso) {
        RutinaHasEjercicio r = new RutinaHasEjercicio(idRutina, idEjercicio);
        r.setSeries(series);
        r.setRepeticiones(repeticiones);
        r.setPeso(peso);
        r.setTiempo(tiempo);
        r.setDescanso(descanso);
        create(r);
    }

    @Override
    public RutinaHasEjercicio find(Object id) {
        return getEntityManager().find(RutinaHasEjercicio.class,
                                       (RutinaHasEjercicioPK) id);
    }
}