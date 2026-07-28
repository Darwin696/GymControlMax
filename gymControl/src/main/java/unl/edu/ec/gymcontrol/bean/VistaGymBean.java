package unl.edu.ec.gymcontrol.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import unl.edu.ec.gymcontrol.domain.Cliente;
import unl.edu.ec.gymcontrol.domain.Empleado;
import unl.edu.ec.gymcontrol.domain.Instructor;
import unl.edu.ec.gymcontrol.domain.Pago;
import unl.edu.ec.gymcontrol.domain.Producto;
import unl.edu.ec.gymcontrol.service.GymService;

@Named("vistaGymBean")
@SessionScoped
public class VistaGymBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private GymService gymService;

    // Planes y check-ins siguen en memoria (aún no tienen entidad)
    private List<PlanVista> planes = new ArrayList<>();
    private List<CheckInVista> checkIns = new ArrayList<>();

    // Form miembro
    private String nuevoNombre, nuevoTelefono, nuevoEmail, nuevoEstado = "Active";
    // Form plan
    private String nuevoPlanNombre, nuevoPlanTipo = "Basic", nuevoPlanDescripcion;
    private double nuevoPlanPrecio;
    // Form instructor
    private String nuevoInstNombre, nuevoInstEspecialidad, nuevoInstHorario;
    // Form empleado
    private String nuevoEmpNombre, nuevoEmpCargo, nuevoEmpTelefono, nuevoEmpEmail;
    private double nuevoEmpSalario;
    // Form pago
    private String nuevoPagoNombre, nuevoPagoPlan, nuevoPagoMetodo = "Card", nuevoPagoEstado = "Paid";
    private double nuevoPagoMonto;
    // Form producto
    private String nuevoProdNombre, nuevoProdCategoria = "Suplemento";
    private double nuevoProdPrecio;
    private int nuevoProdStock;
    // Form venta
    private String ventaCliente, ventaProductoId, ventaMetodo = "Card";
    private int ventaCantidad = 1;

    @PostConstruct
    public void init() {
        planes.add(new PlanVista("Mensual Básico", "Basic", 50.00, "Acceso al Gym", List.of("Locker", "Ducha"), true));
        planes.add(new PlanVista("Trimestral Pro", "Pro", 120.00, "Gym + Piscina", List.of("Piscina", "Locker", "Clases"), true));
        planes.add(new PlanVista("Anual Premium", "Premium", 300.00, "Gym + Piscina + Spa", List.of("Piscina", "Spa", "Clases", "Entrenador"), true));
    }

    private void msg(String texto, boolean error) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true); // ← para que sobreviva el redirect
        ctx.addMessage(null,
                new FacesMessage(error ? FacesMessage.SEVERITY_ERROR : FacesMessage.SEVERITY_INFO, texto, null));
    }

    private String nvl(String s) { return s == null ? "" : s; }
    private String nvl(String s, String d) { return (s == null || s.isBlank()) ? d : s; }

    // ==================== MIEMBROS ====================

    public List<Cliente> getMiembros() {
        try {
            return gymService.listarClientes();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarMiembro() {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            msg("Nombre obligatorio", true);
            return null;
        }
        if (gymService == null) {
            msg("Error: gymService es null (no se inyectó el servicio)", true);
            return null;
        }
        try {
            Cliente c = new Cliente();
            c.setNombre(nuevoNombre.trim());
            c.setTelefono(nvl(nuevoTelefono));
            c.setEmail(nvl(nuevoEmail));
            c.setEstado(nvl(nuevoEstado, "Active"));
            gymService.guardarCliente(c);

            nuevoNombre = nuevoTelefono = nuevoEmail = null;
            nuevoEstado = "Active";
            msg("Miembro guardado en la base de datos", false);
            return "miembros?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarMiembro(Cliente m) {
        try {
            gymService.eliminarCliente(m.getId());
            msg("Miembro eliminado", false);
        } catch (Exception e) {
            msg("Error al eliminar: " + e.getMessage(), true);
        }
        return null;
    }

    // ==================== PRODUCTOS ====================

    public List<Producto> getProductos() {
        try {
            return gymService.listarProductos();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarProducto() {
        if (nuevoProdNombre == null || nuevoProdNombre.isBlank()) {
            msg("Nombre obligatorio", true);
            return null;
        }
        try {
            Producto p = new Producto();
            p.setNombre(nuevoProdNombre.trim());
            p.setCategoria(nuevoProdCategoria);
            p.setPrecio(nuevoProdPrecio);
            p.setStock(nuevoProdStock);
            gymService.guardarProducto(p);

            nuevoProdNombre = null;
            nuevoProdCategoria = "Suplemento";
            nuevoProdPrecio = 0;
            nuevoProdStock = 0;
            msg("Producto guardado en la base de datos", false);
            return "productos?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarProducto(Producto p) {
        try {
            if (p != null && p.getId() != null) {
                Long id = p.getId();
                gymService.eliminarProducto(id);
                msg("Producto eliminado", false);
            }
        } catch (Exception e) {
            msg("Error al eliminar: " + e.getMessage(), true);
        }
        return null;
    }

    public String registrarVenta() {
        if (ventaCliente == null || ventaCliente.isBlank()) {
            msg("Cliente obligatorio", true);
            return null;
        }
        if (ventaProductoId == null || ventaProductoId.isBlank()) {
            msg("Seleccione un producto", true);
            return null;
        }
        try {
            Long prodId = Long.parseLong(ventaProductoId);
            Producto prod = gymService.buscarProducto(prodId);
            if (prod == null) {
                msg("Producto no encontrado", true);
                return null;
            }
            if (prod.getStock() < ventaCantidad) {
                msg("Stock insuficiente de " + prod.getNombre(), true);
                return null;
            }
            prod.setStock(prod.getStock() - ventaCantidad);
            gymService.actualizarProducto(prod);

            double total = prod.getPrecio() * ventaCantidad;
            Pago pago = new Pago(ventaCliente,
                    "Producto: " + prod.getNombre() + " x" + ventaCantidad,
                    nvl(ventaMetodo, "Card"), total, "Paid");
            gymService.guardarPago(pago);

            ventaCliente = ventaProductoId = null;
            ventaCantidad = 1;
            ventaMetodo = "Card";
            msg("Venta registrada. Stock actualizado.", false);
            return "pagos?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public long getProductosBajoStock() {
        return getProductos().stream().filter(p -> p.getStock() < 10).count();
    }

    // ==================== INSTRUCTORES ====================

    public List<Instructor> getInstructores() {
        try {
            return gymService.listarInstructores();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarInstructor() {
        if (nuevoInstNombre == null || nuevoInstNombre.isBlank()) {
            msg("Nombre obligatorio", true);
            return null;
        }
        try {
            Instructor i = new Instructor();
            i.setNombre(nuevoInstNombre.trim());
            i.setEspecialidad(nvl(nuevoInstEspecialidad, "General"));
            i.setHorario(nvl(nuevoInstHorario, "09:00"));
            i.setCargo("Instructor");
            i.setSalario(0);
            gymService.guardarInstructor(i);

            nuevoInstNombre = nuevoInstEspecialidad = nuevoInstHorario = null;
            msg("Instructor guardado en la base de datos", false);
            return "instructores?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarInstructor(Instructor i) {
        try {
            if (i != null && i.getId() != null) {
                gymService.eliminarInstructor(i.getId());
                msg("Instructor eliminado", false);
            }
        } catch (Exception e) {
            msg("Error al eliminar: " + e.getMessage(), true);
        }
        return null;
    }

    // ==================== EMPLEADOS ====================

    public List<Empleado> getEmpleados() {
        try {
            return gymService.listarEmpleados();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarEmpleado() {
        if (nuevoEmpNombre == null || nuevoEmpNombre.isBlank()) {
            msg("Nombre obligatorio", true);
            return null;
        }
        try {
            Empleado e = new Empleado();
            e.setNombre(nuevoEmpNombre.trim());
            e.setTelefono(nvl(nuevoEmpTelefono));
            e.setEmail(nvl(nuevoEmpEmail));
            e.setCargo(nvl(nuevoEmpCargo, "Recepcionista"));
            e.setSalario(nuevoEmpSalario);
            gymService.guardarEmpleado(e);

            nuevoEmpNombre = nuevoEmpCargo = nuevoEmpTelefono = nuevoEmpEmail = null;
            nuevoEmpSalario = 0;
            msg("Empleado guardado en la base de datos", false);
            return "empleados?faces-redirect=true";
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            ex.printStackTrace();
            return null;
        }
    }

    public String eliminarEmpleado(Empleado e) {
        try {
            if (e != null && e.getId() != null) {
                gymService.eliminarEmpleado(e.getId());
                msg("Empleado eliminado", false);
            }
        } catch (Exception ex) {
            msg("Error al eliminar: " + ex.getMessage(), true);
        }
        return null;
    }

    // ==================== PAGOS ====================

    public List<Pago> getPagos() {
        try {
            return gymService.listarPagos();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarPago() {
        if (nuevoPagoNombre == null || nuevoPagoNombre.isBlank()) {
            msg("Cliente obligatorio", true);
            return null;
        }
        if (nuevoPagoMonto <= 0) {
            msg("Monto debe ser mayor a 0", true);
            return null;
        }
        try {
            Pago p = new Pago(nuevoPagoNombre, nvl(nuevoPagoPlan, "Membresía"),
                    nvl(nuevoPagoMetodo, "Card"), nuevoPagoMonto, nvl(nuevoPagoEstado, "Paid"));
            gymService.guardarPago(p);

            nuevoPagoNombre = nuevoPagoPlan = null;
            nuevoPagoMetodo = "Card";
            nuevoPagoMonto = 0;
            nuevoPagoEstado = "Paid";
            msg("Factura registrada en la base de datos", false);
            return "pagos?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarPago(Pago p) {
        try {
            if (p != null && p.getId() != null) {
                gymService.eliminarPago(p.getId());
                msg("Pago eliminado", false);
            }
        } catch (Exception e) {
            msg("Error al eliminar: " + e.getMessage(), true);
        }
        return null;
    }

    public double getTotalRevenue() {
        return getPagos().stream()
                .filter(p -> "Paid".equals(p.getEstado()))
                .mapToDouble(Pago::getMonto).sum();
    }

    public double getPendingBalance() {
        return getPagos().stream()
                .filter(p -> "Pending".equals(p.getEstado()))
                .mapToDouble(Pago::getMonto).sum();
    }

    public long getPendingCount() {
        return getPagos().stream().filter(p -> "Pending".equals(p.getEstado())).count();
    }

    public int getCardShare() {
        List<Pago> lista = getPagos();
        if (lista.isEmpty()) return 0;
        long c = lista.stream().filter(p -> "Card".equals(p.getMetodo())).count();
        return (int) ((c * 100) / lista.size());
    }

    public int getTransferShare() {
        List<Pago> lista = getPagos();
        if (lista.isEmpty()) return 0;
        long t = lista.stream().filter(p -> "Transfer".equals(p.getMetodo())).count();
        return (int) ((t * 100) / lista.size());
    }

    public int getCashShare() {
        List<Pago> lista = getPagos();
        if (lista.isEmpty()) return 0;
        long e = lista.stream().filter(p -> "Cash".equals(p.getMetodo())).count();
        return (int) ((e * 100) / lista.size());
    }

    // ==================== PLANES (memoria) ====================

    public List<PlanVista> getPlanes() { return planes; }

    public String agregarPlan() {
        if (nuevoPlanNombre == null || nuevoPlanNombre.isBlank()) {
            msg("Nombre del plan obligatorio", true);
            return null;
        }
        planes.add(new PlanVista(nuevoPlanNombre, nuevoPlanTipo, nuevoPlanPrecio,
                nvl(nuevoPlanDescripcion), List.of("Acceso básico"), true));
        nuevoPlanNombre = nuevoPlanDescripcion = null;
        nuevoPlanTipo = "Basic";
        nuevoPlanPrecio = 0;
        msg("Plan agregado", false);
        return "planes?faces-redirect=true";
    }

    public String eliminarPlan(PlanVista p) {
        planes.remove(p);
        return null;
    }

    public List<CheckInVista> getCheckIns() {
        List<CheckInVista> lista = new ArrayList<>();
        try {
            List<Cliente> clientes = gymService.listarClientes();
            // últimos 5 miembros (los más recientes al final de la lista)
            int desde = Math.max(0, clientes.size() - 5);
            int minutos = 5;
            for (int i = clientes.size() - 1; i >= desde; i--) {
                Cliente c = clientes.get(i);
                boolean activo = "Active".equalsIgnoreCase(c.getEstado());
                String detalle = activo
                        ? "Active Membership • Gym"
                        : "Paused/Expired Membership";
                String hora = java.time.LocalTime.now()
                        .minusMinutes(minutos)
                        .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                String estado = activo ? "Entry Approved" : "Entry Denied";
                lista.add(new CheckInVista(c.getNombre(), detalle, hora, estado));
                minutos += 20;
            }
        } catch (Exception e) {
            // si falla la BD, lista vacía
        }
        return lista;
    }

    // ==================== Getters / Setters formularios ====================

    public String getNuevoNombre() { return nuevoNombre; }
    public void setNuevoNombre(String v) { nuevoNombre = v; }
    public String getNuevoTelefono() { return nuevoTelefono; }
    public void setNuevoTelefono(String v) { nuevoTelefono = v; }
    public String getNuevoEmail() { return nuevoEmail; }
    public void setNuevoEmail(String v) { nuevoEmail = v; }
    public String getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(String v) { nuevoEstado = v; }

    public String getNuevoPlanNombre() { return nuevoPlanNombre; }
    public void setNuevoPlanNombre(String v) { nuevoPlanNombre = v; }
    public String getNuevoPlanTipo() { return nuevoPlanTipo; }
    public void setNuevoPlanTipo(String v) { nuevoPlanTipo = v; }
    public double getNuevoPlanPrecio() { return nuevoPlanPrecio; }
    public void setNuevoPlanPrecio(double v) { nuevoPlanPrecio = v; }
    public String getNuevoPlanDescripcion() { return nuevoPlanDescripcion; }
    public void setNuevoPlanDescripcion(String v) { nuevoPlanDescripcion = v; }

    public String getNuevoInstNombre() { return nuevoInstNombre; }
    public void setNuevoInstNombre(String v) { nuevoInstNombre = v; }
    public String getNuevoInstEspecialidad() { return nuevoInstEspecialidad; }
    public void setNuevoInstEspecialidad(String v) { nuevoInstEspecialidad = v; }
    public String getNuevoInstHorario() { return nuevoInstHorario; }
    public void setNuevoInstHorario(String v) { nuevoInstHorario = v; }

    public String getNuevoEmpNombre() { return nuevoEmpNombre; }
    public void setNuevoEmpNombre(String v) { nuevoEmpNombre = v; }
    public String getNuevoEmpCargo() { return nuevoEmpCargo; }
    public void setNuevoEmpCargo(String v) { nuevoEmpCargo = v; }
    public String getNuevoEmpTelefono() { return nuevoEmpTelefono; }
    public void setNuevoEmpTelefono(String v) { nuevoEmpTelefono = v; }
    public String getNuevoEmpEmail() { return nuevoEmpEmail; }
    public void setNuevoEmpEmail(String v) { nuevoEmpEmail = v; }
    public double getNuevoEmpSalario() { return nuevoEmpSalario; }
    public void setNuevoEmpSalario(double v) { nuevoEmpSalario = v; }

    public String getNuevoPagoNombre() { return nuevoPagoNombre; }
    public void setNuevoPagoNombre(String v) { nuevoPagoNombre = v; }
    public String getNuevoPagoPlan() { return nuevoPagoPlan; }
    public void setNuevoPagoPlan(String v) { nuevoPagoPlan = v; }
    public String getNuevoPagoMetodo() { return nuevoPagoMetodo; }
    public void setNuevoPagoMetodo(String v) { nuevoPagoMetodo = v; }
    public double getNuevoPagoMonto() { return nuevoPagoMonto; }
    public void setNuevoPagoMonto(double v) { nuevoPagoMonto = v; }
    public String getNuevoPagoEstado() { return nuevoPagoEstado; }
    public void setNuevoPagoEstado(String v) { nuevoPagoEstado = v; }

    public String getNuevoProdNombre() { return nuevoProdNombre; }
    public void setNuevoProdNombre(String v) { nuevoProdNombre = v; }
    public String getNuevoProdCategoria() { return nuevoProdCategoria; }
    public void setNuevoProdCategoria(String v) { nuevoProdCategoria = v; }
    public double getNuevoProdPrecio() { return nuevoProdPrecio; }
    public void setNuevoProdPrecio(double v) { nuevoProdPrecio = v; }
    public int getNuevoProdStock() { return nuevoProdStock; }
    public void setNuevoProdStock(int v) { nuevoProdStock = v; }

    public String getVentaCliente() { return ventaCliente; }
    public void setVentaCliente(String v) { ventaCliente = v; }
    public String getVentaProductoId() { return ventaProductoId; }
    public void setVentaProductoId(String v) { ventaProductoId = v; }
    public int getVentaCantidad() { return ventaCantidad; }
    public void setVentaCantidad(int v) { ventaCantidad = v; }
    public String getVentaMetodo() { return ventaMetodo; }
    public void setVentaMetodo(String v) { ventaMetodo = v; }

    // ==================== Clases internas ====================

    public static class PlanVista {
        private final String nombre, tipo, descripcion;
        private final double precio;
        private final List<String> beneficios;
        private final boolean activo;
        public PlanVista(String n, String t, double p, String d, List<String> b, boolean a) {
            nombre = n; tipo = t; precio = p; descripcion = d; beneficios = b; activo = a;
        }
        public String getNombre() { return nombre; }
        public String getTipo() { return tipo; }
        public double getPrecio() { return precio; }
        public String getDescripcion() { return descripcion; }
        public List<String> getBeneficios() { return beneficios; }
        public boolean isActivo() { return activo; }
    }

    public static class CheckInVista {
        private final String nombre, detalle, hora, estado;
        public CheckInVista(String n, String d, String h, String e) {
            nombre = n; detalle = d; hora = h; estado = e;
        }
        public String getNombre() { return nombre; }
        public String getDetalle() { return detalle; }
        public String getHora() { return hora; }
        public String getEstado() { return estado; }
    }
}