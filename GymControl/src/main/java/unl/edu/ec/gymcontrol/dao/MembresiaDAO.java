package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Membresia;

import java.util.List;

/**
 * DAO concreto para la entidad Membresia (jerarquía Mensual/Anual/VIP).
 * Solo hace acceso a datos; no abre ni cierra transacciones — eso lo
 * controla quien invoque estos métodos (GymService, el Facade).
 */
@ApplicationScoped
public class MembresiaDAO implements GenericDAO<Membresia, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Membresia> listarTodos() {
        return em.createQuery("SELECT m FROM Membresia m ORDER BY m.id", Membresia.class)
                .getResultList();
    }

    public List<Membresia> listarPorCliente(Long clienteId) {
        return em.createQuery(
                        "SELECT m FROM Membresia m WHERE m.cliente.id = :clienteId ORDER BY m.fechaInicio DESC",
                        Membresia.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
    }

    @Override
    public Membresia buscarPorId(Long id) {
        return em.find(Membresia.class, id);
    }

    @Override
    public void guardar(Membresia entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Membresia entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Membresia m = em.find(Membresia.class, id);
        if (m != null) {
            em.remove(m);
        }
    }
}
