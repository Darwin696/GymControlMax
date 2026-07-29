// MembresiaAnual.java
package unl.edu.ec.gymcontrol.domain;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("ANUAL")
public class MembresiaAnual extends Membresia {
    public MembresiaAnual() {}
    public MembresiaAnual(double precio, LocalDate fechaInicio, Cliente cliente) {
        super(precio, fechaInicio, cliente);
    }
    @Override
    protected void calcularFechaVencimiento() {
        this.setFechaVencimiento(this.getFechaInicio().plusYears(1));
    }
}