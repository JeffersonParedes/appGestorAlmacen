package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.SuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import com.gestoralmacen.app.service.SuscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> registrarSuscripcion(@Valid @RequestBody SuscripcionRequestDTO requestDTO) {
        SuscripcionResponseDTO response = suscripcionService.registrarSuscripcion(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/renovar/{empresaId}")
    public ResponseEntity<SuscripcionResponseDTO> renovarSuscripcion(@PathVariable Long empresaId, @Valid @RequestBody SuscripcionRequestDTO requestDTO) {
        SuscripcionResponseDTO response = suscripcionService.renovarSuscripcion(empresaId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-vencer")
    public ResponseEntity<List<SuscripcionResponseDTO>> consultarSuscripcionesPorVencer(@RequestParam(defaultValue = "7") int dias) {
        List<SuscripcionResponseDTO> response = suscripcionService.consultarSuscripcionesPorVencer(dias);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> consultarHistorial(@PathVariable Long empresaId) {
        List<SuscripcionResponseDTO> response = suscripcionService.consultarHistorial(empresaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/actualizar-estados")
    public ResponseEntity<Void> actualizarEstadoSuscripciones() {
        suscripcionService.actualizarEstadoSuscripciones();
        return ResponseEntity.noContent().build();
    }
}
