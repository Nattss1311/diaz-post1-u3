package com.universidad.confudes.certificados;

public abstract class DecoradorCertificado implements ServicioCertificados {

    protected final ServicioCertificados servicioDecorado;

    public DecoradorCertificado(ServicioCertificados servicioDecorado) {
        this.servicioDecorado = servicioDecorado;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        return servicioDecorado.emitir(solicitud);
    }
}