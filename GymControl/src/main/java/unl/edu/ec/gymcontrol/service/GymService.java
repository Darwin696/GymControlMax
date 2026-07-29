package unl.edu.ec.gymcontrol.service;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

import unl.edu.ec.gymcontrol.dao.*;
import unl.edu.ec.gymcontrol.domain.*;

/**
 * Capa de SERVICIO (reglas de negocio + control de transacciones).
 */
@ApplicationScoped
public class GymService {

    @PersistenceContext(unitName = "gymPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    // ==================== DAOs ====================
    @Inject private ClienteDAO clienteDAO;
    @Inject private MembresiaDAO membresiaDAO;
    @Inject private PlanDAO planDAO;
    @Inject private ProductoDAO productoDAO;
    @Inject private InstructorDAO instructorDAO;
    @Inject private EmpleadoDAO empleadoDAO;
    @Inject private PagoDAO pagoDAO;

    // ==================== Helper de transacción ====================
    private void tx(Runnable action) {
        try {
            utx.begin();
            em.joinTransaction();
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

    // ==================== CLIENTES ====================
    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public void guardarCliente(Cliente c) {
        tx(() -> clienteDAO.guardar(c));
    }

    public void eliminarCliente(Long id) {
        tx(() -> clienteDAO.eliminar(id));
    }

    public Cliente buscarCliente(Long id) {
        return clienteDAO.buscarPorId(id);
    }

    /**
     * Actualiza el estado del cliente según si tiene al menos una membresía activa.
     */
    public void actualizarEstadoCliente(Long clienteId) {
        tx(() -> {
            Cliente cliente = clienteDAO.buscarPorId(clienteId);
            if (cliente == null) return;

            List<Membresia> membresias = membresiaDAO.listarPorCliente(clienteId);

            boolean tieneActiva = membresias.stream()
                    .anyMatch(m -> m.esActiva());

            cliente.setEstado(tieneActiva ? "Active" : "Expired");
            clienteDAO.actualizar(cliente);
        });
    }

    // ==================== MEMBRESÍAS ====================
    public List<Membresia> listarMembresias() {
        return membresiaDAO.listarTodos();
    }

    public List<Membresia> listarMembresiasDeCliente(Long clienteId) {
        return membresiaDAO.listarPorCliente(clienteId);
    }

    public void asignarMembresia(Cliente cliente, Membresia nuevaMembresia) {
        tx(() -> {
            cliente.agregarMembresia(nuevaMembresia);
            membresiaDAO.guardar(nuevaMembresia);
        });
        // Actualizar estado del cliente
        actualizarEstadoCliente(cliente.getId());
    }

    public void renovarMembresia(Long membresiaId) {
        tx(() -> {
            Membresia m = membresiaDAO.buscarPorId(membresiaId);
            if (m != null) {
                m.renovar();
                membresiaDAO.actualizar(m);
            }
        });
        Membresia m = membresiaDAO.buscarPorId(membresiaId);
        if (m != null && m.getCliente() != null) {
            actualizarEstadoCliente(m.getCliente().getId());
        }
    }

    public void eliminarMembresia(Long id) {
        Membresia m = membresiaDAO.buscarPorId(id);
        if (m == null) return;

        Long clienteId = m.getCliente().getId();
        tx(() -> membresiaDAO.eliminar(id));
        actualizarEstadoCliente(clienteId);
    }

    // ==================== PLANES ====================
    public List<Plan> listarPlanes() {
        return planDAO.listarTodos();
    }

    public void guardarPlan(Plan p) {
        tx(() -> planDAO.guardar(p));
    }

    public void eliminarPlan(Long id) {
        tx(() -> planDAO.eliminar(id));
    }

    // ==================== PRODUCTOS ====================
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

    // ==================== INSTRUCTORES ====================
    public List<Instructor> listarInstructores() {
        return instructorDAO.listarTodos();
    }

    public void guardarInstructor(Instructor i) {
        tx(() -> instructorDAO.guardar(i));
    }

    public void eliminarInstructor(Long id) {
        tx(() -> instructorDAO.eliminar(id));
    }

    // ==================== EMPLEADOS ====================
    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listarTodos();
    }

    public void guardarEmpleado(Empleado e) {
        tx(() -> empleadoDAO.guardar(e));
    }

    public void eliminarEmpleado(Long id) {
        tx(() -> empleadoDAO.eliminar(id));
    }

    // ==================== PAGOS ====================
    public List<Pago> listarPagos() {
        return pagoDAO.listarTodos();
    }

    public void guardarPago(Pago p) {
        tx(() -> pagoDAO.guardar(p));
    }

    public void eliminarPago(Long id) {
        tx(() -> pagoDAO.eliminar(id));
    }

    public void marcarPagoComoPagado(Long id) {
        tx(() -> {
            Pago p = pagoDAO.buscarPorId(id);
            if (p != null) {
                p.setEstado("Paid");
                pagoDAO.actualizar(p);
            }
        });
    }

    public Plan buscarPlan(Long id) {
        //lógica de búsqueda por id
        return em.find(Plan.class, id);
    }

}