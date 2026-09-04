package com.universidad.confudes.certificados;

public class DecoradorCodigoQR extends DecoradorCertificado {

    private final String urlVerificacion;

    public DecoradorCodigoQR(ServicioCertificados servicioDecorado, String urlVerificacion) {
        super(servicioDecorado);
        this.urlVerificacion = urlVerificacion;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] docOriginal = super.emitir(solicitud);
        return UtilidadesPDF.insertarCodigoQR(docOriginal, urlVerificacion);
    }
}