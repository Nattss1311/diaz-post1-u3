package com.universidad.confudes.certificados;

public class DecoradorTraduccionIngles extends DecoradorCertificado {

    public DecoradorTraduccionIngles(ServicioCertificados servicioDecorado) {
        super(servicioDecorado);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] docOriginal = super.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(docOriginal);
    }
}