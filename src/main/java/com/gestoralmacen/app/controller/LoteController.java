package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.LoteRequestDTO;
import com.gestoralmacen.app.dto.response.LoteResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService loteService;
    private final SecurityContextHelper securityHelper;

    public LoteController(LoteService loteService, SecurityContextHelper securityHelper) {
        this.loteService = loteService;
        this.securityHelper = securityHelper;
    }

    @PostMapping
    public ResponseEntity<LoteResponseDTO> crearLote(@Valid @RequestBody LoteRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        LoteResponseDTO response = loteService.crearLote(requestDTO, empresaId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponseDTO> obtenerLotePorId(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        LoteResponseDTO response = loteService.obtenerLotePorId(id, empresaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<LoteResponseDTO>> listarLotesPorProducto(@PathVariable Long productoId) {
        Long empresaId = securityHelper.getEmpresaId();
        List<LoteResponseDTO> response = loteService.listarLotesPorProducto(productoId, empresaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/por-vencer")
    public ResponseEntity<List<LoteResponseDTO>> consultarLotesPorVencer(@RequestParam(defaultValue = "30") int dias) {
        Long empresaId = securityHelper.getEmpresaId();
        List<LoteResponseDTO> response = loteService.consultarLotesPorVencer(empresaId, dias);
        return ResponseEntity.ok(response);
    }
}
