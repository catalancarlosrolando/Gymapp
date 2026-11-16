package sesiones;

import entidad.Rutina;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RutinaFacade extends AbstractFacade<Rutina> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public RutinaFacade() { super(Rutina.class); }
    
        /**
     * Crea la rutina y hace flush explícito para garantizar que
     * el ID autogenerado (@GeneratedValue IDENTITY) quede cargado
     * en la entidad ANTES de retornar al caller.
     *
     * Sin este flush, llamar a r.getIdrutina() inmediatamente después
     * de create() puede devolver null porque EclipseLink no necessarily
     * hace flush dentro de la tx hasta el commit.
     */
    public void createConFlush(Rutina r) {
        em.persist(r);
        em.flush();   // fuerza el INSERT y carga el ID autogenerado
    }

    /** Todas las rutinas de un perfil (ordenadas por id descendente). */
    public List<Rutina> findByPerfil(Integer idPerfil) {
        return em.createNamedQuery("Rutina.findByPerfil", Rutina.class)
                 .setParameter("idperfil", idPerfil)
                 .getResultList();
    }

    /** Rutinas pendientes (realizada = false) de un perfil. */
    public List<Rutina> findPendientesByPerfil(Integer idPerfil) {
        return em.createNamedQuery("Rutina.findPendientesByPerfil", Rutina.class)
                 .setParameter("idperfil", idPerfil)
                 .getResultList();
    }

    /** Rutina con sus ejercicios cargados (evita LazyInit en JSP). */
    public Rutina findConEjercicios(Integer idRutina) {
        try {
            return em.createQuery(
                    "SELECT r FROM Rutina r LEFT JOIN FETCH r.rutinaHasEjercicioList rh "
                  + "LEFT JOIN FETCH rh.ejercicio "
                  + "WHERE r.idrutina = :id", Rutina.class)
                     .setParameter("id", idRutina)
                     .setHint("jakarta.persistence.cacheRetrieveMode", "REFRESH")
                     .setHint("jakarta.persistence.cacheStoreMode", "REFRESH")
                     .getSingleResult();
        } catch (Exception ex) {
            return find(idRutina);
        }
    }
}