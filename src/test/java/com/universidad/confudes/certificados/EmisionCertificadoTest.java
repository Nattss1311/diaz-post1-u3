package com.universidad.confudes.certificados;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();

        ServicioCertificados colaborador = new FachadaCertificados(validador, generador, firma, correo);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Natalia Díaz", "natalia@example.com");

        assertDoesNotThrow(() -> {
            colaborador.emitir(solicitud);
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}