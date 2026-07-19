package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> crearEmpresa(@Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO nuevaEmpresa = empresaService.crearEmpresa(requestDTO);
        return new ResponseEntity<>(nuevaEmpresa, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        empresaService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> actualizarEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO actualizada = empresaService.actualizarEmpresa(id, requestDTO);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/{id}/info-general")
    public ResponseEntity<java.util.Map<String, Object>> consultarInformacionGeneral(@PathVariable Long id) {
        java.util.Map<String, Object> stats = empresaService.consultarInformacionGeneral(id);
        return ResponseEntity.ok(stats);
    }
}