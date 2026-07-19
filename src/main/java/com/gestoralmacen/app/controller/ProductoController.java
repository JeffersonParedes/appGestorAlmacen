package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.ProductoRequestDTO;
import com.gestoralmacen.app.dto.response.ProductoResponseDTO;
import com.gestoralmacen.app.service.ProductoService;
import com.gestoralmacen.app.security.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final SecurityContextHelper securityHelper;

    // 1. CORREGIDO: Se agregó SecurityContextHelper a los parámetros del
    // constructor
    public ProductoController(ProductoService productoService, SecurityContextHelper securityHelper) {
        this.productoService = productoService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
        // 2. El ID se extrae automáticamente del Token de la sesión actual
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(productoService.listarActivosPorEmpresa(empresaId));
    }

    @GetMapping("/papelera")
    public ResponseEntity<List<ProductoResponseDTO>> listarBorrados() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(productoService.listarBorradosPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> sugerirProducto(
            @Valid @RequestBody ProductoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        ProductoResponseDTO sugerido = productoService.sugerirProducto(requestDTO, empresaId);
        return new ResponseEntity<>(sugerido, HttpStatus.CREATED);
    }

    @PostMapping("/registrar")
    public ResponseEntity<ProductoResponseDTO> registrarProducto(
            @Valid @RequestBody ProductoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        ProductoResponseDTO registrado = productoService.registrarProducto(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(registrado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id, @Valid @RequestBody ProductoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, requestDTO, empresaId);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        ProductoResponseDTO producto = productoService.obtenerPorId(id, empresaId);
        return ResponseEntity.ok(producto);
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Void> aprobarProducto(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        productoService.aprobarProducto(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Void> rechazarProducto(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        productoService.rechazarProducto(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        productoService.eliminarProducto(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-minimo")
    public ResponseEntity<List<ProductoResponseDTO>> consultarProductosConStockMinimo() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(productoService.consultarProductosConStockMinimo(empresaId));
    }

    @GetMapping("/proximos-vencer")
    public ResponseEntity<List<ProductoResponseDTO>> consultarProductosProximosAVencer(@RequestParam(defaultValue = "30") int dias) {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(productoService.consultarProductosProximosAVencer(empresaId, dias));
    }
}

