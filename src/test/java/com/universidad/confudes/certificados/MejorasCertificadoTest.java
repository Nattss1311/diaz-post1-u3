package com.universidad.confudes.certificados;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class MejorasCertificadoTest {

    private final SolicitudCertificado solicitud =
        new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

    private ServicioCertificados crearServicioBase() {
        return new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
    }

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = crearServicioBase();
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados base = crearServicioBase();
        ServicioCertificados conTodo = new DecoradorTraduccionIngles(
                new DecoradorCodigoQR(
                        new DecoradorMarcaDeAgua(base, "CONFUDES 2026"),
                        "https://confudes.com/verificar"
                )
        );
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados base = crearServicioBase();
        ServicioCertificados soloMarcaDeAgua = new DecoradorMarcaDeAgua(base, "CONFUDES 2026");
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }
}