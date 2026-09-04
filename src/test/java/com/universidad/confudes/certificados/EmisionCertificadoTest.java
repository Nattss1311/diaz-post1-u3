package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();

        FachadaCertificados colaborador = new FachadaCertificados(validador, generador, firma, correo);
        
        // Invocar el método público de la fachada
        assertDoesNotThrow(() -> {
            colaborador.emitirCertificado("EVT-001", "PART-123", "Natalia Díaz", "natalia@example.com");
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        // Verificación de diseño: ControladorCertificados debe declarar un solo
        // constructor con un solo parámetro tras la refactorización.
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}