package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "membresia")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_membresia")
public abstract class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precio;

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
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.cliente = cliente;
        this.estado = EstadoMembresia.ACTIVA;
        calcularFechaVencimiento();
    }

    protected abstract void calcularFechaVencimiento();

    public boolean esActiva() {
        return estado == EstadoMembresia.ACTIVA &&
                (fechaVencimiento == null || !LocalDate.now().isAfter(fechaVencimiento));
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
