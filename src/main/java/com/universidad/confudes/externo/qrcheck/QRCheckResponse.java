package com.universidad.confudes.externo.qrcheck;

public class QRCheckResponse {
    private final int codigo;
    private final String detalle;

    public QRCheckResponse(int codigo, String detalle) {
        this.codigo = codigo;
        this.detalle = detalle;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDetalle() {
        return detalle;
    }
}