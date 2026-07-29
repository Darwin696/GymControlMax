package unl.edu.ec.gymcontrol.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del polimorfismo en la jerarquía de Membresia.
 * Cada subclase sobrescribe calcularFechaVencimiento() con su propia regla
 * de negocio; estas pruebas verifican que, invocando el método a través de
 * una referencia de tipo Membresia (polimorfismo), se ejecute la
 * implementación correcta de cada subclase en tiempo de ejecución.
 */
class MembresiaTest {

    private final Cliente cliente = new Cliente();
    private final LocalDate inicio = LocalDate.of(2026, 1, 15);

    @Test
    void membresiaMensualVenceEnUnMes() {
        Membresia m = new MembresiaMensual(20.0, inicio, cliente);
        assertEquals(LocalDate.of(2026, 2, 15), m.getFechaVencimiento());
    }

    @Test
    void membresiaAnualVenceEnUnAnio() {
        Membresia m = new MembresiaAnual(200.0, inicio, cliente);
        assertEquals(LocalDate.of(2027, 1, 15), m.getFechaVencimiento());
    }

    @Test
    void membresiaVipVenceEnUnAnio() {
        Membresia m = new MembresiaVIP(350.0, inicio, cliente);
        assertEquals(LocalDate.of(2027, 1, 15), m.getFechaVencimiento());
    }

    @Test
    void membresiaNuevaQuedaActiva() {
        Membresia m = new MembresiaMensual(20.0, LocalDate.now(), cliente);
        assertTrue(m.esActiva());
        assertEquals(EstadoMembresia.ACTIVA, m.getEstado());
    }

    @Test
    void membresiaVencidaNoEstaActiva() {
        // fechaInicio muy en el pasado -> fechaVencimiento ya pasó
        Membresia m = new MembresiaMensual(20.0, LocalDate.now().minusMonths(3), cliente);
        assertFalse(m.esActiva());
    }

    @Test
    void renovarActualizaFechaInicioYVencimiento() {
        Membresia m = new MembresiaMensual(20.0, LocalDate.now().minusMonths(3), cliente);
        assertFalse(m.esActiva());

        m.renovar();

        assertTrue(m.esActiva());
        assertEquals(EstadoMembresia.ACTIVA, m.getEstado());
        assertEquals(LocalDate.now().plusMonths(1), m.getFechaVencimiento());
    }

    @Test
    void cadaSubclaseCalculaSuPropiaVigenciaAlLlamarPorReferenciaBase() {
        // Se guardan distintas subclases bajo la misma referencia Membresia:
        // esto es exactamente el polimorfismo que hay que defender en la sustentación.
        Membresia mensual = new MembresiaMensual(20.0, inicio, cliente);
        Membresia anual = new MembresiaAnual(200.0, inicio, cliente);
        Membresia vip = new MembresiaVIP(350.0, inicio, cliente);

        assertNotEquals(mensual.getFechaVencimiento(), anual.getFechaVencimiento());
        assertEquals(anual.getFechaVencimiento(), vip.getFechaVencimiento());
    }
}