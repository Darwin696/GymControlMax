package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Instructor;

import java.util.List;

/**
 * DAO concreto para la entidad Instructor.
 */
@ApplicationScoped
public class InstructorDAO implements GenericDAO<Instructor, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Instructor> listarTodos() {
        return em.createQuery("SELECT i FROM Instructor i ORDER BY i.id", Instructor.class)
                .getResultList();
    }

    @Override
    public Instructor buscarPorId(Long id) {
        return em.find(Instructor.class, id);
    }

    @Override
    public void guardar(Instructor entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Instructor entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Instructor i = em.find(Instructor.class, id);
        if (i != null) {
            em.remove(i);
        }
    }
}
