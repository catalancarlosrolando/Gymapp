package sesiones;

import entidad.Cuenta;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CuentaFacade extends AbstractFacade<Cuenta> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public CuentaFacade() { super(Cuenta.class); }

    /**
     * Login por username + password.
     * Devuelve null si las credenciales son inválidas.
     */
    public Cuenta buscarPorCredenciales(String username, String password) {
        try {
            return em.createQuery(
                    "SELECT c FROM Cuenta c WHERE c.username = :u AND c.password = :p",
                    Cuenta.class)
                    .setParameter("u", username)
                    .setParameter("p", password)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}