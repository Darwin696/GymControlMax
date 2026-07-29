package unl.edu.ec.gymcontrol.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import unl.edu.ec.gymcontrol.domain.*;
import unl.edu.ec.gymcontrol.service.GymService;

@Named("vistaGymBean")
@SessionScoped
public class VistaGymBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private GymService gymService;

    // Form miembro
    private Cliente nuevoCliente = new Cliente();

    // Form membresía
    private Cliente clienteSeleccionado;
    private Plan planSeleccionado;
    private double nuevaMembresiaPrecio;
    private EstadoMembresia estadoSeleccionado = EstadoMembresia.ACTIVA;

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
        // Ya no se cargan planes en memoria
    }

    private void msg(String texto, boolean error) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
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
        try {
            gymService.guardarCliente(nuevoCliente);
            nuevoCliente = new Cliente();
            msg("Miembro guardado en la base de datos", false);
            return "miembros?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
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

    // ==================== MEMBRESÍAS ====================

    public List<Membresia> getMembresias() {
        try {
            return gymService.listarMembresias();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Plan> getPlanesDisponibles() {
        try {
            return gymService.listarPlanes();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String asignarMembresia() {
        if (clienteSeleccionado == null) {
            msg("Seleccione un cliente", true);
            return null;
        }
        if (planSeleccionado == null) {
            msg("Seleccione un plan", true);
            return null;
        }
        if (nuevaMembresiaPrecio <= 0) {
            msg("El precio debe ser mayor a 0", true);
            return null;
        }

        // Protección: si el estado llega null, forzamos ACTIVA
        if (estadoSeleccionado == null) {
            estadoSeleccionado = EstadoMembresia.ACTIVA;
        }

        try {
            String tipo = planSeleccionado.getTipo() != null
                    ? planSeleccionado.getTipo().toUpperCase()
                    : "BASIC";

            Membresia nueva;
            if (tipo.contains("ANUAL") || tipo.contains("PREMIUM") || tipo.contains("VIP")) {
                nueva = new MembresiaAnual(nuevaMembresiaPrecio, LocalDate.now(), clienteSeleccionado);
            } else if (tipo.contains("TRIMESTRAL") || tipo.contains("PRO")) {
                nueva = new MembresiaAnual(nuevaMembresiaPrecio, LocalDate.now(), clienteSeleccionado);
            } else {
                nueva = new MembresiaMensual(nuevaMembresiaPrecio, LocalDate.now(), clienteSeleccionado);
            }

            nueva.setEstado(estadoSeleccionado);
            gymService.asignarMembresia(clienteSeleccionado, nueva);

            // Limpiar
            clienteSeleccionado = null;
            planSeleccionado = null;
            nuevaMembresiaPrecio = 0;
            estadoSeleccionado = EstadoMembresia.ACTIVA;

            msg("Membresía asignada correctamente", false);
            return "membresias?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String renovarMembresia(Membresia m) {
        try {
            gymService.renovarMembresia(m.getId());
            msg("Membresía renovada", false);
        } catch (Exception e) {
            msg("Error al renovar: " + e.getMessage(), true);
        }
        return null;
    }

    public String eliminarMembresia(Membresia m) {
        try {
            gymService.eliminarMembresia(m.getId());
            msg("Membresía eliminada", false);
        } catch (Exception e) {
            msg("Error al eliminar: " + e.getMessage(), true);
        }
        return null;
    }

    // ==================== PLANES ====================

    public List<Plan> getPlanes() {
        try {
            return gymService.listarPlanes();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String agregarPlan() {
        if (nuevoPlanNombre == null || nuevoPlanNombre.isBlank()) {
            msg("Nombre del plan obligatorio", true);
            return null;
        }
        try {
            Plan plan = new Plan(
                    nuevoPlanNombre.trim(),
                    nvl(nuevoPlanTipo, "Basic"),
                    nuevoPlanPrecio,
                    nvl(nuevoPlanDescripcion)
            );
            gymService.guardarPlan(plan);

            nuevoPlanNombre = nuevoPlanDescripcion = null;
            nuevoPlanTipo = "Basic";
            nuevoPlanPrecio = 0;
            msg("Plan guardado en la base de datos", false);
            return "planes?faces-redirect=true";
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarPlan(Plan p) {
        try {
            if (p != null && p.getId() != null) {
                gymService.eliminarPlan(p.getId());
                msg("Plan eliminado", false);
            }
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
            while (root.getCause() != null) root = root.getCause();
            msg("Error al guardar: " + root.getClass().getSimpleName() + " - " + root.getMessage(), true);
            e.printStackTrace();
            return null;
        }
    }

    public String eliminarProducto(Producto p) {
        try {
            if (p != null && p.getId() != null) {
                gymService.eliminarProducto(p.getId());
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
            while (root.getCause() != null) root = root.getCause();
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
            while (root.getCause() != null) root = root.getCause();
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
            while (root.getCause() != null) root = root.getCause();
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
            while (root.getCause() != null) root = root.getCause();
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

    public String marcarPagoPagado(Pago p) {
        try {
            if (p != null && p.getId() != null) {
                gymService.marcarPagoComoPagado(p.getId());
                msg("Pago marcado como Pagado", false);
            }
        } catch (Exception e) {
            msg("Error: " + e.getMessage(), true);
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

    // ==================== CHECK-INS ====================

    public List<CheckInVista> getCheckIns() {
        List<CheckInVista> lista = new ArrayList<>();
        try {
            List<Cliente> clientes = gymService.listarClientes();
            int desde = Math.max(0, clientes.size() - 5);
            int minutos = 5;
            for (int i = clientes.size() - 1; i >= desde; i--) {
                Cliente c = clientes.get(i);
                boolean activo = "Active".equalsIgnoreCase(c.getEstado());
                String detalle = activo ? "Active Membership • Gym" : "Paused/Expired Membership";
                String hora = java.time.LocalTime.now()
                        .minusMinutes(minutos)
                        .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                String estado = activo ? "Entry Approved" : "Entry Denied";
                lista.add(new CheckInVista(c.getNombre(), detalle, hora, estado));
                minutos += 20;
            }
        } catch (Exception e) {
            // silencioso
        }
        return lista;
    }

    // ==================== Getters / Setters ====================

    public Cliente getNuevoCliente() { return nuevoCliente; }
    public void setNuevoCliente(Cliente v) { nuevoCliente = v; }

    public Cliente getClienteSeleccionado() { return clienteSeleccionado; }
    public void setClienteSeleccionado(Cliente v) { clienteSeleccionado = v; }

    public Plan getPlanSeleccionado() { return planSeleccionado; }
    public void setPlanSeleccionado(Plan plan) {
        this.planSeleccionado = plan;
        if (plan != null) {
            this.nuevaMembresiaPrecio = plan.getPrecio();
        } else {
            this.nuevaMembresiaPrecio = 0;
        }
    }

    public double getNuevaMembresiaPrecio() { return nuevaMembresiaPrecio; }
    public void setNuevaMembresiaPrecio(double v) { nuevaMembresiaPrecio = v; }

    public EstadoMembresia getEstadoSeleccionado() { return estadoSeleccionado; }
    public void setEstadoSeleccionado(EstadoMembresia v) { estadoSeleccionado = v; }

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

    // ==================== Clase interna ====================

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