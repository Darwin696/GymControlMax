package unl.edu.ec.gymcontrol.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import unl.edu.ec.gymcontrol.domain.Pago;

import java.util.List;

/**
 * DAO concreto para la entidad Pago.
 * No se usa "actualizar" en el negocio actual (los pagos no se editan,
 * solo se registran o se eliminan), pero se implementa igual para
 * cumplir el contrato GenericDAO por completo.
 */
@ApplicationScoped
public class PagoDAO implements GenericDAO<Pago, Long> {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Override
    public List<Pago> listarTodos() {
        return em.createQuery("SELECT p FROM Pago p ORDER BY p.id DESC", Pago.class)
                .getResultList();
    }

    @Override
    public Pago buscarPorId(Long id) {
        return em.find(Pago.class, id);
    }

    @Override
    public void guardar(Pago entity) {
        em.persist(entity);
    }

    @Override
    public void actualizar(Pago entity) {
        em.merge(entity);
    }

    @Override
    public void eliminar(Long id) {
        Pago p = em.find(Pago.class, id);
        if (p != null) {
            em.remove(p);
        }
    }
}
