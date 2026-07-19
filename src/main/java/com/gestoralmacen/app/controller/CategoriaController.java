package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.CategoriaRequestDTO;
import com.gestoralmacen.app.dto.response.CategoriaResponseDTO;
import com.gestoralmacen.app.service.CategoriaService;
import com.gestoralmacen.app.security.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final SecurityContextHelper securityHelper;

    // Inyectamos el Helper en el constructor
    public CategoriaController(CategoriaService categoriaService, SecurityContextHelper securityHelper) {
        this.categoriaService = categoriaService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarActivas() {
        // Extraemos el ID de forma segura desde el Token
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(categoriaService.listarActivasPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> sugerirCategoria(
            @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        // Extraemos el ID de forma segura desde el Token
        Long empresaId = securityHelper.getEmpresaId();
        CategoriaResponseDTO sugerida = categoriaService.sugerirCategoria(requestDTO, empresaId);
        return new ResponseEntity<>(sugerida, HttpStatus.CREATED);
    }

    @PostMapping("/registrar")
    public ResponseEntity<CategoriaResponseDTO> registrarCategoria(
            @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        CategoriaResponseDTO registrada = categoriaService.registrarCategoria(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(registrada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        CategoriaResponseDTO actualizada = categoriaService.actualizarCategoria(id, requestDTO, empresaId);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        CategoriaResponseDTO categoria = categoriaService.obtenerPorId(id, empresaId);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(categoriaService.listarTodasPorEmpresa(empresaId));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Void> aprobarCategoria(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        categoriaService.aprobarCategoria(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Void> rechazarCategoria(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        categoriaService.rechazarCategoria(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        categoriaService.eliminarCategoria(id, empresaId);
        return ResponseEntity.noContent().build();
    }
}