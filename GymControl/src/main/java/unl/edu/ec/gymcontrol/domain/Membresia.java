package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "membresia")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_membresia")
public abstract class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "El precio de la membresía debe ser mayor a 0")
    private double precio;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private EstadoMembresia estado = EstadoMembresia.ACTIVA;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Membresia() {}

    public Membresia(double precio, LocalDate fechaInicio, Cliente cliente) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio de la membresía debe ser mayor a 0");
        }
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Toda membresía debe estar asociada a un cliente");
        }
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.cliente = cliente;
        this.estado = EstadoMembresia.ACTIVA;
        calcularFechaVencimiento();
        if (fechaVencimiento.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
        }
    }

    protected abstract void calcularFechaVencimiento();

    public String getTipo() {
        return this.getClass().getSimpleName().replace("Membresia", "");
    }

    public boolean esActiva() {
        boolean vigente = fechaVencimiento == null || !LocalDate.now().isAfter(fechaVencimiento);

        if (!vigente && estado == EstadoMembresia.ACTIVA) {
            this.estado = EstadoMembresia.VENCIDA; // se marca como vencida
        }

        return estado == EstadoMembresia.ACTIVA && vigente;
    }

    public void renovar() {
        this.fechaInicio = LocalDate.now();
        calcularFechaVencimiento();
        this.estado = EstadoMembresia.ACTIVA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public EstadoMembresia getEstado() { return estado; }
    public void setEstado(EstadoMembresia estado) { this.estado = estado; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
