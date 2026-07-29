package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Empleado;

import java.util.List;

/**
 * DAO concreto para la entidad Empleado.
 *
 * IMPORTANTE: como Instructor EXTIENDE Empleado (herencia JOINED), una
 * consulta ingenua "SELECT e FROM Empleado e" también traería a los
 * Instructor (son Empleado también, polimórficamente). Como los
 * instructores ya tienen su propia pantalla, aquí filtramos con
 * TYPE(e) = Empleado para listar solo el personal que NO es instructor
 * (ej. recepcionistas).
 */
@ApplicationScoped
public class EmpleadoDAO implements GenericDAO<Empleado, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Empleado> listarTodos() {
        return em.createQuery(
                        "SELECT e FROM Empleado e WHERE TYPE(e) = Empleado ORDER BY e.id",
                        Empleado.class)
                .getResultList();
    }

    @Override
    public Empleado buscarPorId(Long id) {
        return em.find(Empleado.class, id);
    }

    @Override
    public void guardar(Empleado entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Empleado entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Empleado e = em.find(Empleado.class, id);
        if (e != null) {
            em.remove(e);
        }
    }
}
