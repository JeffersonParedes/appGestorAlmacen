package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/validaciones")
public class ValidacionController {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    public ValidacionController(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Validaciones de Empresa ---

    @GetMapping("/empresa/ruc")
    public ResponseEntity<Map<String, Object>> validarRuc(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && empresaRepository.existsByRuc(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "RUC ya registrado." : null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/empresa/correo")
    public ResponseEntity<Map<String, Object>> validarCorreoEmpresa(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && empresaRepository.existsByCorreoContacto(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "El correo empresarial ya existe." : null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/empresa/telefono")
    public ResponseEntity<Map<String, Object>> validarTelefonoEmpresa(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && empresaRepository.existsByTelefonoContacto(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "El número telefónico ya pertenece a otra empresa." : null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/empresa/direccion")
    public ResponseEntity<Map<String, Object>> validarDireccionEmpresa(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && empresaRepository.existsByDireccionPrincipal(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "La dirección ingresada ya se encuentra registrada." : null);
        return ResponseEntity.ok(res);
    }

    // --- Validaciones de Usuario Bodeguero ---

    @GetMapping("/usuario/nombre")
    public ResponseEntity<Map<String, Object>> validarNombreUsuario(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && usuarioRepository.existsByUsuario(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "El usuario ya se encuentra registrado." : null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/usuario/correo")
    public ResponseEntity<Map<String, Object>> validarCorreoUsuario(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && usuarioRepository.existsByCorreo(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "El correo electrónico ya existe." : null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/usuario/dni")
    public ResponseEntity<Map<String, Object>> validarDniUsuario(@RequestParam String valor) {
        boolean existe = valor != null && !valor.trim().isEmpty() && usuarioRepository.existsByDni(valor.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("existe", existe);
        res.put("mensaje", existe ? "El DNI ingresado ya pertenece a otro usuario." : null);
        return ResponseEntity.ok(res);
    }
}
