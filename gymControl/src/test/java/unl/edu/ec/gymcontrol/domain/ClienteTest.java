package unl.edu.ec.gymcontrol.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de encapsulamiento y de la relación Cliente-Membresia
 * (@OneToMany mappedBy "cliente"), que debe quedar sincronizada en ambos
 * sentidos al usar el método agregarMembresia().
 */
class ClienteTest {

    @Test
    void agregarMembresiaLaAgregaALaListaDelCliente() {
        Cliente cliente = new Cliente();
        Membresia m = new MembresiaMensual(20.0, LocalDate.now(), cliente);

        cliente.agregarMembresia(m);

        assertEquals(1, cliente.getMembresias().size());
        assertTrue(cliente.getMembresias().contains(m));
    }

    @Test
    void agregarMembresiaEstableceLaReferenciaInversaAlCliente() {
        Cliente cliente = new Cliente();
        Membresia m = new MembresiaAnual(200.0, LocalDate.now(), cliente);

        cliente.agregarMembresia(m);

        // La relación bidireccional debe quedar consistente en ambos lados
        assertSame(cliente, m.getCliente());
    }

    @Test
    void clienteNuevoIniciaConEstadoActivoYSinMembresias() {
        Cliente cliente = new Cliente();

        assertEquals("Active", cliente.getEstado());
        assertTrue(cliente.getMembresias().isEmpty());
        assertEquals(LocalDate.now(), cliente.getFechaRegistro());
    }

    @Test
    void getNombreYSetNombreRespetanEncapsulamiento() {
        // Persona expone nombre solo mediante getters/setters (atributo private)
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana Torres");

        assertEquals("Ana Torres", cliente.getNombre());
    }
}