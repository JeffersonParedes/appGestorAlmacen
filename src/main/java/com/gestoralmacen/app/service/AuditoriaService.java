package com.gestoralmacen.app.service;

public interface AuditoriaService {
    void registrar(Long usuarioId, Long administradorId, Long empresaId, String accion, String tablaAfectada, Long registroId, String descripcion, String ip);
}
