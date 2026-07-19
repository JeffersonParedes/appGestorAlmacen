package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.MovimientoRequestDTO;
import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.dto.response.MovimientoResponseDTO;
import com.gestoralmacen.app.service.MovimientoService;
import com.gestoralmacen.app.security.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final SecurityContextHelper securityHelper;

    // Inyectamos nuestro Helper de seguridad
    public MovimientoController(MovimientoService movimientoService, SecurityContextHelper securityHelper) {
        this.movimientoService = movimientoService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/kardex")
    public ResponseEntity<List<MovimientoResponseDTO>> listarKardex() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(movimientoService.listarHistorialPorEmpresa(empresaId));
    }

    @GetMapping("/inventario")
    public ResponseEntity<List<InventarioResponseDTO>> listarInventario() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(movimientoService.listarInventarioPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrarMovimiento(
            @Valid @RequestBody MovimientoRequestDTO requestDTO) {

        // Obtenemos empresa y usuario directamente de la sesión actual (JWT)
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();

        MovimientoResponseDTO movimiento = movimientoService.registrarMovimiento(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoResponseDTO> registrarEntrada(
            @Valid @RequestBody MovimientoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        MovimientoResponseDTO movimiento = movimientoService.registrarEntrada(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoResponseDTO> registrarSalida(
            @Valid @RequestBody MovimientoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        MovimientoResponseDTO movimiento = movimientoService.registrarSalida(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
    }

    @PostMapping("/traslado")
    public ResponseEntity<MovimientoResponseDTO> registrarTraslado(
            @Valid @RequestBody MovimientoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        MovimientoResponseDTO movimiento = movimientoService.registrarTraslado(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
    }

    @PostMapping("/ajuste")
    public ResponseEntity<MovimientoResponseDTO> registrarAjuste(
            @Valid @RequestBody MovimientoRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        Long usuarioId = securityHelper.getUsuarioId();
        MovimientoResponseDTO movimiento = movimientoService.registrarAjuste(requestDTO, empresaId, usuarioId);
        return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
    }
}