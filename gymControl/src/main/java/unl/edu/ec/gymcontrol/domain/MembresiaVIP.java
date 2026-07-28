// MembresiaVIP.java
package unl.edu.ec.gymcontrol.domain;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("VIP")
public class MembresiaVIP extends Membresia {
    public MembresiaVIP() {}
    public MembresiaVIP(double precio, LocalDate fechaInicio, Cliente cliente) {
        super(precio, fechaInicio, cliente);
    }
    @Override
    protected void calcularFechaVencimiento() {
        this.setFechaVencimiento(this.getFechaInicio().plusYears(1));
    }
}