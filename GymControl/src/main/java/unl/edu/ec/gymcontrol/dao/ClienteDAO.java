package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Cliente;

import java.util.List;

/**
 * DAO concreto para la entidad Cliente.
 * Solo hace acceso a datos (SELECT / persist / remove); no abre ni
 * cierra transacciones — la transacción activa la controla quien
 * invoque estos métodos (típicamente GymService).
 */
@ApplicationScoped
public class ClienteDAO implements GenericDAO<Cliente, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Cliente> listarTodos() {
        return em.createQuery("SELECT c FROM Cliente c ORDER BY c.id", Cliente.class)
                .getResultList();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return em.find(Cliente.class, id);
    }

    @Override
    public void guardar(Cliente entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Cliente entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Cliente c = em.find(Cliente.class, id);
        if (c != null) {
            em.remove(c);
        }
    }
}
