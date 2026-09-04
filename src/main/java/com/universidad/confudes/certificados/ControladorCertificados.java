package com.universidad.confudes.certificados;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    private final FachadaCertificados fachadaCertificados;

    public ControladorCertificados(FachadaCertificados fachadaCertificados) {
        this.fachadaCertificados = fachadaCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                          @RequestParam String nombre, @RequestParam String correoDestino) {
        boolean exito = fachadaCertificados.emitirCertificado(eventoId, participanteId, nombre, correoDestino);
        
        return exito 
            ? ResponseEntity.ok("Certificado emitido y enviado")
            : ResponseEntity.status(403).body("Asistencia insuficiente");
    }
}