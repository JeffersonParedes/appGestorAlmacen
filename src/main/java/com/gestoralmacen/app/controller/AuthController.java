package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.AuthRequestDTO;
import com.gestoralmacen.app.dto.response.AuthResponseDTO;
import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.repository.AdministradorRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        // 1. Spring Security verifica si el usuario y contraseña coinciden en la BD
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsuario(), request.getContrasena()));

        // 2. Si la contraseña es correcta, obtenemos los detalles del usuario logueado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Buscamos primero al usuario en la BD
        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername()).orElse(null);
        if (usuario != null) {
            Long empresaId = (usuario.getEmpresa() != null) ? usuario.getEmpresa().getId() : null;
            String token = jwtUtil.generarToken(
                    usuario.getUsuario(),
                    usuario.getId(),
                    empresaId,
                    usuario.getRol());
            return ResponseEntity.ok(new AuthResponseDTO(token));
        }

        // 4. Si no es usuario, es administrador
        Administrador admin = administradorRepository.findByUsuario(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado en la base de datos"));
        
        String token = jwtUtil.generarToken(
                admin.getUsuario(),
                admin.getId(),
                null, // Los administradores no tienen empresa_id
                "ADMINISTRADOR");

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}