package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Plan;

import java.util.List;

@ApplicationScoped
public class PlanDAO implements GenericDAO<Plan, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Plan> listarTodos() {
        return em.createQuery("SELECT p FROM Plan p ORDER BY p.id", Plan.class)
                .getResultList();
    }

    @Override
    public Plan buscarPorId(Long id) {
        return em.find(Plan.class, id);
    }

    @Override
    public void guardar(Plan entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Plan entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Plan p = em.find(Plan.class, id);
        if (p != null) {
            em.remove(p);
        }
    }
}