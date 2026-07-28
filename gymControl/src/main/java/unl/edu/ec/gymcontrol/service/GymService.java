package unl.edu.ec.gymcontrol.service;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import unl.edu.ec.gymcontrol.dao.ClienteDAO;
import unl.edu.ec.gymcontrol.dao.EmpleadoDAO;
import unl.edu.ec.gymcontrol.dao.InstructorDAO;
import unl.edu.ec.gymcontrol.dao.PagoDAO;
import unl.edu.ec.gymcontrol.dao.ProductoDAO;
import unl.edu.ec.gymcontrol.domain.Cliente;
import unl.edu.ec.gymcontrol.domain.Empleado;
import unl.edu.ec.gymcontrol.domain.Instructor;
import unl.edu.ec.gymcontrol.domain.Pago;
import unl.edu.ec.gymcontrol.domain.Producto;

/**
 * Capa de SERVICIO (reglas de negocio + control de transacciones).
 *
 * IMPORTANTE: esta clase ya NO llama a EntityManager.persist/remove/createQuery
 * directamente. Todo el acceso a datos se delega a los DAO (ClienteDAO,
 * ProductoDAO, InstructorDAO, PagoDAO) inyectados abajo. GymService solo
 * decide CUÁNDO abrir/cerrar una transacción (con UserTransaction) y en qué
 * orden se llaman los DAO — esa es la separación de responsabilidades del
 * patrón DAO: "DAO = cómo se guarda un dato" vs "Service = cuándo y por qué
 * se guarda".
 */
@ApplicationScoped
public class GymService {

    // Se mantiene un EntityManager propio del servicio SOLO para poder
    // unir manualmente la transacción (em.joinTransaction()). Los DAO usan
    // su propio EntityManager inyectado, pero al compartir la misma unidad
    // de persistencia ("gymPU") dentro de la misma transacción JTA activa,
    // el contenedor garantiza que todos ven el mismo contexto de persistencia.
    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private InstructorDAO instructorDAO;

    @Inject
    private EmpleadoDAO empleadoDAO;

    @Inject
    private PagoDAO pagoDAO;

    // ---------- helper de transacción ----------
    private void tx(Runnable action) {
        try {
            utx.begin();
            em.joinTransaction();   // importante
            action.run();
            utx.commit();
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ignored) {}
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            throw new RuntimeException(
                    root.getClass().getSimpleName() + ": " + root.getMessage(), e
            );
        }
    }

    // ---------- CLIENTES ----------
    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public void guardarCliente(Cliente c) {
        tx(() -> clienteDAO.guardar(c));
    }

    public void eliminarCliente(Long id) {
        tx(() -> clienteDAO.eliminar(id));
    }

    // ---------- PRODUCTOS ----------
    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    public void guardarProducto(Producto p) {
        tx(() -> productoDAO.guardar(p));
    }

    public void actualizarProducto(Producto p) {
        tx(() -> productoDAO.actualizar(p));
    }

    public Producto buscarProducto(Long id) {
        return productoDAO.buscarPorId(id);
    }

    public void eliminarProducto(Long id) {
        tx(() -> productoDAO.eliminar(id));
    }

    // ---------- INSTRUCTORES ----------
    public List<Instructor> listarInstructores() {
        return instructorDAO.listarTodos();
    }

    public void guardarInstructor(Instructor i) {
        tx(() -> instructorDAO.guardar(i));
    }

    public void eliminarInstructor(Long id) {
        tx(() -> instructorDAO.eliminar(id));
    }

    // ---------- EMPLEADOS ----------
    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listarTodos();
    }

    public void guardarEmpleado(Empleado e) {
        tx(() -> empleadoDAO.guardar(e));
    }

    public void eliminarEmpleado(Long id) {
        tx(() -> empleadoDAO.eliminar(id));
    }

    // ---------- PAGOS ----------
    public List<Pago> listarPagos() {
        return pagoDAO.listarTodos();
    }

    public void guardarPago(Pago p) {
        tx(() -> pagoDAO.guardar(p));
    }

    public void eliminarPago(Long id) {
        tx(() -> pagoDAO.eliminar(id));
    }
}
