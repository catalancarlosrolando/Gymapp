package sesiones;

import entidad.Plan;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class PlanFacade extends AbstractFacade<Plan> {

    @PersistenceContext(unitName = "AppGimnasio")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    public PlanFacade() { super(Plan.class); }
}