package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.UsuarioRequestDTO;
import com.gestoralmacen.app.dto.response.UsuarioResponseDTO;
import com.gestoralmacen.app.service.UsuarioService;
import com.gestoralmacen.app.security.SecurityContextHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SecurityContextHelper securityHelper;

    // Inyectamos el Helper en el constructor
    public UsuarioController(UsuarioService usuarioService, SecurityContextHelper securityHelper) {
        this.usuarioService = usuarioService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorEmpresa() {
        // Extraemos el ID de la empresa del token
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {

        // ¡Magia doble! Sacamos tanto la empresa como quién es el creador directamente
        // del Token
        Long empresaId = securityHelper.getEmpresaId();
        Long creadorId = securityHelper.getUsuarioId();

        UsuarioResponseDTO nuevoUsuario = usuarioService.crearUsuario(requestDTO, empresaId, creadorId);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        // En un futuro, podrías validar aquí que el usuario que intenta borrar tenga
        // rol "ADMIN"
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable Long id) {
        usuarioService.activarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO actualizado = usuarioService.actualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(actualizado);
    }
}