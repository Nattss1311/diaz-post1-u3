package com.universidad.confudes.certificados;

public class DecoradorMarcaDeAgua extends DecoradorCertificado {

    private final String textoMarca;

    public DecoradorMarcaDeAgua(ServicioCertificados servicioDecorado, String textoMarca) {
        super(servicioDecorado);
        this.textoMarca = textoMarca;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] docOriginal = super.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(docOriginal, textoMarca);
    }
}