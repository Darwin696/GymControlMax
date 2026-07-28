package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
@DiscriminatorValue("EMPLEADO")
public class Empleado extends Persona {

    private String cargo;
    private double salario;

    public Empleado() {}

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
}
