package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Producto;

import java.util.List;

/**
 * DAO concreto para la entidad Producto.
 */
@ApplicationScoped
public class ProductoDAO implements GenericDAO<Producto, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Producto> listarTodos() {
        return em.createQuery("SELECT p FROM Producto p ORDER BY p.id", Producto.class)
                .getResultList();
    }

    @Override
    public Producto buscarPorId(Long id) {
        return em.find(Producto.class, id);
    }

    @Override
    public void guardar(Producto entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Producto entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Producto p = em.find(Producto.class, id);
        if (p != null) {
            em.remove(p);
        }
    }
}
