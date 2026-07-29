// MembresiaMensual.java
package unl.edu.ec.gymcontrol.domain;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("MENSUAL")
public class MembresiaMensual extends Membresia {
    public MembresiaMensual() {}
    public MembresiaMensual(double precio, LocalDate fechaInicio, Cliente cliente) {
        super(precio, fechaInicio, cliente);
    }
    @Override
    protected void calcularFechaVencimiento() {
        this.setFechaVencimiento(this.getFechaInicio().plusMonths(1));
    }
}