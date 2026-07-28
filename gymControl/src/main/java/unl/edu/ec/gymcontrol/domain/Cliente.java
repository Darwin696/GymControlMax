package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@DiscriminatorValue("CLIENTE")
public class Cliente extends Persona {

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro = LocalDate.now();
    private String estado = "Active"; // Active / Expired

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membresia> membresias = new ArrayList<>();

    public Cliente() {}

    public void agregarMembresia(Membresia m) {
        membresias.add(m);
        m.setCliente(this);
    }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<Membresia> getMembresias() { return membresias; }
}
