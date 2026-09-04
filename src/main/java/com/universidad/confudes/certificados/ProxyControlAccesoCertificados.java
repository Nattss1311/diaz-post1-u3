package com.universidad.confudes.certificados;

import com.universidad.confudes.acceso.ContextoUsuario;

public class ProxyControlAccesoCertificados implements ServicioCertificados {

    private final ServicioCertificados servicioReal;

    public ProxyControlAccesoCertificados(ServicioCertificados servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (!"ORGANIZADOR".equals(rol) && !"ADMIN".equals(rol)) {
            throw new SecurityException("Acceso denegado: Se requiere rol ORGANIZADOR o ADMIN para ejecutar esta operación.");
        }
        return servicioReal.emitir(solicitud);
    }
}