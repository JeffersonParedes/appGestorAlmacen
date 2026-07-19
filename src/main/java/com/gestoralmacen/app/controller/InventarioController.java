package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final SecurityContextHelper securityHelper;

    public InventarioController(InventarioService inventarioService, SecurityContextHelper securityHelper) {
        this.inventarioService = inventarioService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarPorEmpresa() {
        Long empresaId = securityHelper.getEmpresaId();
        List<InventarioResponseDTO> response = inventarioService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<List<InventarioResponseDTO>> listarPorAlmacen(@PathVariable Long almacenId) {
        Long empresaId = securityHelper.getEmpresaId();
        List<InventarioResponseDTO> response = inventarioService.listarPorAlmacen(almacenId, empresaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<InventarioResponseDTO> obtenerPorProductoYAlmacen(@PathVariable Long productoId, @PathVariable Long almacenId) {
        Long empresaId = securityHelper.getEmpresaId();
        InventarioResponseDTO response = inventarioService.obtenerPorProductoYAlmacen(productoId, almacenId, empresaId);
        return ResponseEntity.ok(response);
    }
}
