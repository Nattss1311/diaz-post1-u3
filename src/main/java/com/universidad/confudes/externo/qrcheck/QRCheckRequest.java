package com.universidad.confudes.externo.qrcheck;

public class QRCheckRequest {
    private final String payload;

    public QRCheckRequest(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    } 
}