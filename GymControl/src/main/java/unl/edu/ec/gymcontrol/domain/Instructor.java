package unl.edu.ec.gymcontrol.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "instructor")
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends Empleado {

    private String especialidad;
    private String horario;
    private String puntuacion = "5.0";
    private int clientes = 0;
    private String estatus = "Active";
    private boolean activo = true;

    @Column(name = "image_url")
    private String imageUrl = "https://images.unsplash.com/photo-1518611012118-696072aa579a";

    public Instructor() {}

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getPuntuacion() { return puntuacion; }
    public void setPuntuacion(String puntuacion) { this.puntuacion = puntuacion; }

    public int getClientes() { return clientes; }
    public void setClientes(int clientes) { this.clientes = clientes; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
