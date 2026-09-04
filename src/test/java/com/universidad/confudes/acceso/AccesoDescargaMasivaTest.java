package com.universidad.confudes.acceso;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.universidad.confudes.certificados.EnvioCorreoService;
import com.universidad.confudes.certificados.FachadaCertificados;
import com.universidad.confudes.certificados.FirmaDigitalService;
import com.universidad.confudes.certificados.GeneradorCertificadoPDF;
import com.universidad.confudes.certificados.ProxyControlAccesoCertificados;
import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;
import com.universidad.confudes.certificados.ValidadorAsistencia;

class AccesoDescargaMasivaTest {

    @AfterEach
    void limpiarRol() {
        System.clearProperty("confudes.rol");
    }

    private ServicioCertificados crearServicioBase() {
        return new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
    }

    @Test
    void rechazaAParticipanteSinLlegarAEmitir() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        ServicioCertificados base = crearServicioBase();
        ServicioCertificados controlado = new ProxyControlAccesoCertificados(base);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
    }

    @Test
    void permiteAOrganizador() {
        System.setProperty("confudes.rol", "ORGANIZADOR");
        ServicioCertificados base = crearServicioBase();
        ServicioCertificados controlado = new ProxyControlAccesoCertificados(base);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }
}