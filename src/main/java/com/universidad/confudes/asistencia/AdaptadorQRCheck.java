package com.universidad.confudes.asistencia;

import org.springframework.stereotype.Service;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;

@Service
public class AdaptadorQRCheck implements ServicioAsistencia {

    private final QRCheckClient qrCheckClient;

    public AdaptadorQRCheck() {
        this.qrCheckClient = new QRCheckClient();
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        // Pasar la credencial tal cual llega al SDK del proveedor
        QRCheckRequest request = new QRCheckRequest(credencialQR);
        QRCheckResponse response = qrCheckClient.validar(request);

        boolean exitoso = (response.getCodigo() == 200);
        return new ResultadoCheckIn(exitoso, response.getDetalle());
    }
}