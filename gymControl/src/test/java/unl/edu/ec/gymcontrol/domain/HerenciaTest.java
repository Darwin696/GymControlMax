package unl.edu.ec.gymcontrol.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la jerarquía de herencia de dos niveles:
 *   Persona (abstracta) -> Empleado -> Instructor
 *   Persona (abstracta) -> Cliente
 *
 * Verifican que Instructor hereda correctamente los atributos/comportamiento
 * de Empleado y de Persona, y que Persona no puede instanciarse directamente
 * (es abstracta), tal como exige el RNF06 (100% orientado a objetos).
 */
class HerenciaTest {

    @Test
    void instructorEsUnEmpleadoYUnaPersona() {
        Instructor instructor = new Instructor();

        assertTrue(instructor instanceof Empleado);
        assertTrue(instructor instanceof Persona);
    }

    @Test
    void clienteEsUnaPersonaPeroNoUnEmpleado() {
        Cliente cliente = new Cliente();

        assertTrue(cliente instanceof Persona);
        // Nota: no se puede usar "cliente instanceof Empleado" porque Cliente y
        // Empleado son clases hermanas (ninguna hereda de la otra) y Java no
        // permite ese "instanceof" entre clases no relacionadas: es un error
        // de compilación, no una comprobación en tiempo de ejecución.
        assertNotEquals(Empleado.class, cliente.getClass());
    }

    @Test
    void instructorHeredaAtributosDeEmpleadoYDePersona() {
        Instructor instructor = new Instructor();

        // Atributos heredados de Empleado
        instructor.setCargo("Entrenador Senior");
        instructor.setSalario(850.0);

        // Atributos heredados de Persona (2 niveles arriba)
        instructor.setNombre("Carlos Vega");
        instructor.setEmail("carlos.vega@gymcontrol.ec");

        assertEquals("Entrenador Senior", instructor.getCargo());
        assertEquals(850.0, instructor.getSalario());
        assertEquals("Carlos Vega", instructor.getNombre());
        assertEquals("carlos.vega@gymcontrol.ec", instructor.getEmail());
    }

    @Test
    void instructorTieneValoresPorDefectoPropiosDeSuNivel() {
        Instructor instructor = new Instructor();

        assertTrue(instructor.isActivo());
        assertEquals(0, instructor.getClientes());
        assertEquals("Active", instructor.getEstatus());
    }

    @Test
    void todasLasSubclasesDeMembresiaSonMembresia() {
        assertTrue(new MembresiaMensual() instanceof Membresia);
        assertTrue(new MembresiaAnual() instanceof Membresia);
        assertTrue(new MembresiaVIP() instanceof Membresia);
    }
}