package unl.edu.ec.gymcontrol.dao;

import java.util.List;

/**
 * Contrato genérico del patrón DAO (Data Access Object).
 * Cada DAO concreto encapsula el acceso a datos de UNA entidad,
 * usando el EntityManager (JPA) directamente. No maneja transacciones:
 * eso es responsabilidad de la capa de servicio (GymService), que decide
 * CUÁNDO empieza y termina una transacción de negocio.
 *
 * @param <T>  tipo de la entidad (ej. Cliente, Producto)
 * @param <ID> tipo de la clave primaria (ej. Long)
 */
public interface GenericDAO<T, ID> {

    List<T> listarTodos();

    T buscarPorId(ID id);

    void guardar(T entity);

    void actualizar(T entity);

    void eliminar(ID id);
}
