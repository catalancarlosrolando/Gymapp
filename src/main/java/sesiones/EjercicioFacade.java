package sesiones;

import entidad.Ejercicio;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class EjercicioFacade extends AbstractFacade<Ejercicio> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public EjercicioFacade() { super(Ejercicio.class); }
}