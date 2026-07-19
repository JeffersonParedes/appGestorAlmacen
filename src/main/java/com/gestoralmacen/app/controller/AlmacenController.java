package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.AlmacenRequestDTO;
import com.gestoralmacen.app.dto.response.AlmacenResponseDTO;
import com.gestoralmacen.app.service.AlmacenService;
import com.gestoralmacen.app.security.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/almacenes")
public class AlmacenController {

    private final AlmacenService almacenService;
    private final SecurityContextHelper securityHelper;

    // Inyectamos nuestro Helper de seguridad
    public AlmacenController(AlmacenService almacenService, SecurityContextHelper securityHelper) {
        this.almacenService = almacenService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<AlmacenResponseDTO>> listarPorEmpresa() {
        // Extraemos el ID de forma segura desde el Token
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(almacenService.listarPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<AlmacenResponseDTO> crearAlmacen(@Valid @RequestBody AlmacenRequestDTO requestDTO) {
        // Extraemos el ID de forma segura desde el Token
        Long empresaId = securityHelper.getEmpresaId();
        AlmacenResponseDTO nuevoAlmacen = almacenService.crearAlmacen(requestDTO, empresaId);
        return new ResponseEntity<>(nuevoAlmacen, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlmacen(@PathVariable Long id) {
        // Extraemos el ID de forma segura desde el Token
        Long empresaId = securityHelper.getEmpresaId();
        almacenService.eliminarAlmacen(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlmacenResponseDTO> actualizarAlmacen(@PathVariable Long id, @Valid @RequestBody AlmacenRequestDTO requestDTO) {
        Long empresaId = securityHelper.getEmpresaId();
        AlmacenResponseDTO actualizado = almacenService.actualizarAlmacen(id, requestDTO, empresaId);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarAlmacen(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        almacenService.activarAlmacen(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarAlmacen(@PathVariable Long id) {
        Long empresaId = securityHelper.getEmpresaId();
        almacenService.desactivarAlmacen(id, empresaId);
        return ResponseEntity.noContent().build();
    }
}