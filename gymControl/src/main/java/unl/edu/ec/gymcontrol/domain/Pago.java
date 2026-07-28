package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_cliente")
    private String nombreCliente;
    private String concepto;      // plan o producto
    private String metodo;        // Card, Transfer, Cash
    private double monto;
    private LocalDate fecha = LocalDate.now();
    private String estado;        // Paid, Pending

    public Pago() {}

    public Pago(String nombreCliente, String concepto, String metodo, double monto, String estado) {
        this.nombreCliente = nombreCliente;
        this.concepto = concepto;
        this.metodo = metodo;
        this.monto = monto;
        this.estado = estado;
        this.fecha = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
