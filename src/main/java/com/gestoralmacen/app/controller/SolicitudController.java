package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.SolicitudRequestDTO;
import com.gestoralmacen.app.dto.response.SolicitudResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SecurityContextHelper securityHelper;

    public SolicitudController(SolicitudService solicitudService, SecurityContextHelper securityHelper) {
        this.solicitudService = solicitudService;
        this.securityHelper = securityHelper;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crearSolicitud(@Valid @RequestBody SolicitudRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        SolicitudResponseDTO response = solicitudService.crearSolicitud(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerPorId(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        SolicitudResponseDTO response = solicitudService.obtenerPorId(id, empresaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> listarPorEmpresa() {
        Long empresaId = securityHelper.getEmpresaId();
        List<SolicitudResponseDTO> response = solicitudService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/procesar")
    public ResponseEntity<Void> procesarSolicitud(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String motivoRechazo) {
        Long empresaId = securityHelper.getEmpresaId();
        solicitudService.procesarSolicitud(id, estado, motivoRechazo, empresaId);
        return ResponseEntity.noContent().build();
    }
}
