package sesiones;

import entidad.Perfil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PerfilFacade extends AbstractFacade<Perfil> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public PerfilFacade() { super(Perfil.class); }

    /** Todos los perfiles de una cuenta (ordenados por id). */
    public List<Perfil> findByCuenta(Integer idCuenta) {
        return em.createNamedQuery("Perfil.findByCuenta", Perfil.class)
                 .setParameter("idcuenta", idCuenta)
                 .getResultList();
    }

    /** Cuántos perfiles tiene una cuenta (para validar max_perfiles). */
    public Long countByCuenta(Integer idCuenta) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Perfil p WHERE p.cuenta.idcuenta = :id", Long.class)
                 .setParameter("id", idCuenta)
                 .getSingleResult();
    }

    /** Devuelve un perfil con sus relaciones cargadas (cuenta + plan) para evitar LazyInit. */
    public Perfil findConCuenta(Integer idPerfil) {
        try {
            return em.createQuery(
                    "SELECT p FROM Perfil p LEFT JOIN FETCH p.cuenta c LEFT JOIN FETCH c.plan "
                  + "WHERE p.idperfil = :id", Perfil.class)
                     .setParameter("id", idPerfil)
                     .getSingleResult();
        } catch (Exception ex) {
            return find(idPerfil);
        }
    }
}