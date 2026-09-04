package com.universidad.confudes.asistencia;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Ya en producción — no modificar. Depende únicamente de ServicioAsistencia.
@RestController
@RequestMapping("/api/checkin")
public class ControladorCheckIn {
    private final ServicioAsistencia servicioAsistencia;

    public ControladorCheckIn(ServicioAsistencia servicioAsistencia) {
        this.servicioAsistencia = servicioAsistencia;
    }

    @PostMapping
    public ResponseEntity<ResultadoCheckIn> registrar(@RequestParam String eventoId,
                                                        @RequestParam String participanteId,
                                                        @RequestParam String credencialQR) {
        ResultadoCheckIn resultado =
            servicioAsistencia.registrarAsistencia(eventoId, participanteId, credencialQR);
        return resultado.isExitoso() ? ResponseEntity.ok(resultado) : ResponseEntity.status(422).body(resultado);
    }
}