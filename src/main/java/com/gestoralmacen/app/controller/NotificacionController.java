package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final SecurityContextHelper securityHelper;

    public NotificacionController(NotificacionService notificacionService, SecurityContextHelper securityHelper) {
        this.notificacionService = notificacionService;
        this.securityHelper = securityHelper;
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<NotificacionResponseDTO>> consultarPorUsuario() {
        Long usuarioId = securityHelper.getUsuarioId();
        List<NotificacionResponseDTO> response = notificacionService.consultarPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<NotificacionResponseDTO>> consultarPorAdministrador() {
        Long adminId = securityHelper.getUsuarioId();
        List<NotificacionResponseDTO> response = notificacionService.consultarPorAdministrador(adminId);
        return ResponseEntity.ok(response);
    }
}
