package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.request.RegistroEmpresaSaaSRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import com.gestoralmacen.app.service.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @PostMapping("/empresas")
    public ResponseEntity<EmpresaResponseDTO> registrarEmpresaSaaS(@Valid @RequestBody RegistroEmpresaSaaSRequestDTO request) {
        EmpresaResponseDTO response = administradorService.registrarEmpresaSaaS(
                request.getEmpresa(),
                request.getUsuarioBodeguero(),
                request.getCorreoBodeguero(),
                request.getContrasenaBodeguero(),
                request.getDniBodeguero(),
                request.getNombreBodeguero(),
                request.getPlanSuscripcion(),
                request.getMontoPago(),
                request.getDuracionMeses()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/empresas/{id}/suspender")
    public ResponseEntity<Void> suspenderEmpresa(@PathVariable Long id) {
        administradorService.suspenderEmpresa(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/empresas/{id}/reactivar")
    public ResponseEntity<Void> reactivarEmpresa(@PathVariable Long id) {
        administradorService.reactivarEmpresa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> consultarEstadisticasSaaS() {
        Map<String, Object> stats = administradorService.consultarEstadisticasSaaS();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/suscripciones")
    public ResponseEntity<List<SuscripcionResponseDTO>> consultarSuscripciones() {
        List<SuscripcionResponseDTO> suscripciones = administradorService.consultarSuscripciones();
        return ResponseEntity.ok(suscripciones);
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<Void> gestionarNotificacion(@Valid @RequestBody NotificacionRequestDTO request) {
        administradorService.gestionarNotificacion(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
