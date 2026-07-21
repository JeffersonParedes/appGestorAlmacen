package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import com.gestoralmacen.app.entity.*;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.EmpresaMapper;
import com.gestoralmacen.app.mapper.SuscripcionMapper;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.service.AdministradorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final NotificacionRepository notificacionRepository;
    private final EmpresaMapper empresaMapper;
    private final SuscripcionMapper suscripcionMapper;
    private final PasswordEncoder passwordEncoder;

    public AdministradorServiceImpl(EmpresaRepository empresaRepository,
                                    UsuarioRepository usuarioRepository,
                                    SuscripcionRepository suscripcionRepository,
                                    NotificacionRepository notificacionRepository,
                                    EmpresaMapper empresaMapper,
                                    SuscripcionMapper suscripcionMapper,
                                    PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.notificacionRepository = notificacionRepository;
        this.empresaMapper = empresaMapper;
        this.suscripcionMapper = suscripcionMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmpresaResponseDTO registrarEmpresaSaaS(EmpresaRequestDTO empresaDTO, 
                                                    String usuarioBodeguero, 
                                                    String correoBodeguero, 
                                                    String contrasenaBodeguero, 
                                                    String dniBodeguero, 
                                                    String nombreBodeguero, 
                                                    String planSuscripcion, 
                                                    Double montoPago, 
                                                    Integer duracionMeses) {
        
        // 1. Validaciones de Duplicados de Empresa
        if (empresaDTO.getRuc() != null && empresaRepository.existsByRuc(empresaDTO.getRuc().trim())) {
            throw new ReglaNegocioException("RUC ya registrado.");
        }
        if (empresaDTO.getCorreoContacto() != null && !empresaDTO.getCorreoContacto().trim().isEmpty() 
                && empresaRepository.existsByCorreoContacto(empresaDTO.getCorreoContacto().trim())) {
            throw new ReglaNegocioException("El correo empresarial ya existe.");
        }
        if (empresaDTO.getTelefonoContacto() != null && !empresaDTO.getTelefonoContacto().trim().isEmpty() 
                && empresaRepository.existsByTelefonoContacto(empresaDTO.getTelefonoContacto().trim())) {
            throw new ReglaNegocioException("El número telefónico ya pertenece a otra empresa.");
        }
        if (empresaDTO.getDireccionPrincipal() != null && !empresaDTO.getDireccionPrincipal().trim().isEmpty() 
                && empresaRepository.existsByDireccionPrincipal(empresaDTO.getDireccionPrincipal().trim())) {
            throw new ReglaNegocioException("La dirección ingresada ya se encuentra registrada.");
        }

        // 2. Validaciones de Duplicados de Usuario Bodeguero
        if (usuarioBodeguero != null && usuarioRepository.existsByUsuario(usuarioBodeguero.trim())) {
            throw new ReglaNegocioException("El usuario ya se encuentra registrado.");
        }
        if (correoBodeguero != null && !correoBodeguero.trim().isEmpty() 
                && usuarioRepository.existsByCorreo(correoBodeguero.trim())) {
            throw new ReglaNegocioException("El correo electrónico ya existe.");
        }
        if (dniBodeguero != null && !dniBodeguero.trim().isEmpty() 
                && usuarioRepository.existsByDni(dniBodeguero.trim())) {
            throw new ReglaNegocioException("El DNI ingresado ya pertenece a otro usuario.");
        }

        // 3. Creación y persistencia de Empresa
        Empresa empresa = empresaMapper.toEntity(empresaDTO);
        empresa.setEstado("ACTIVO");
        Empresa empresaGuardada = empresaRepository.save(empresa);

        // 4. Creación y persistencia de Usuario Bodeguero
        Usuario bodeguero = new Usuario();
        bodeguero.setUsuario(usuarioBodeguero != null ? usuarioBodeguero.trim() : "");
        bodeguero.setCorreo(correoBodeguero != null ? correoBodeguero.trim() : "");
        bodeguero.setContrasena(passwordEncoder.encode(contrasenaBodeguero));
        bodeguero.setDni(dniBodeguero != null ? dniBodeguero.trim() : "");
        bodeguero.setNombreCompleto(nombreBodeguero != null ? nombreBodeguero.trim() : "");
        bodeguero.setRol("BODEGUERO");
        bodeguero.setActivo(true);
        bodeguero.setEmpresa(empresaGuardada);
        usuarioRepository.save(bodeguero);

        // 5. Normalización de Plan y Cálculos Automáticos de Tarifas/Fechas
        String planStr = (planSuscripcion != null) ? planSuscripcion.toUpperCase() : "BASICO";
        String planFinal = "BASICO";
        BigDecimal montoFinal = new BigDecimal("15.00");
        int mesesDuracion = 1;

        if (planStr.contains("PREMIUM")) {
            planFinal = "PREMIUM";
            montoFinal = new BigDecimal("150.00");
            mesesDuracion = 12;
        } else if (planStr.contains("PRO")) {
            planFinal = "PRO";
            montoFinal = new BigDecimal("85.00");
            mesesDuracion = 6;
        } else {
            planFinal = "BASICO";
            montoFinal = new BigDecimal("15.00");
            mesesDuracion = 1;
        }

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusMonths(mesesDuracion);

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setEmpresa(empresaGuardada);
        suscripcion.setPlanSuscripcion(planFinal);
        suscripcion.setTipoSuscripcion("PRIMER_REGISTRO");
        suscripcion.setMetodoPago(planSuscripcion != null ? planSuscripcion : "TRANSFERENCIA");
        suscripcion.setFechaInicio(fechaInicio);
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstadoPago("PAGADO");
        suscripcion.setMontoPagado(montoFinal);
        suscripcionRepository.save(suscripcion);

        return empresaMapper.toResponse(empresaGuardada);
    }

    @Override
    @Transactional
    public void suspenderEmpresa(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada con ID: " + empresaId));
        empresa.setEstado("SUSPENDIDO");
        empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public void reactivarEmpresa(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada con ID: " + empresaId));
        empresa.setEstado("ACTIVO");
        empresaRepository.save(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> consultarEstadisticasSaaS() {
        long totalEmpresas = empresaRepository.count();
        long totalActivas = empresaRepository.findAll().stream().filter(e -> "ACTIVO".equals(e.getEstado())).count();
        long totalSuspendidas = empresaRepository.findAll().stream().filter(e -> "SUSPENDIDO".equals(e.getEstado())).count();
        
        BigDecimal ingresosTotales = suscripcionRepository.findAll().stream()
                .filter(s -> "PAGADO".equals(s.getEstadoPago()))
                .map(Suscripcion::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmpresas", totalEmpresas);
        stats.put("totalActivas", totalActivas);
        stats.put("totalSuspendidas", totalSuspendidas);
        stats.put("ingresosTotales", ingresosTotales);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> consultarSuscripciones() {
        return suscripcionRepository.findAll().stream()
                .map(suscripcionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void gestionarNotificacion(NotificacionRequestDTO dto) {
        Notificacion notificacion = new Notificacion();
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId()).orElse(null);
            notificacion.setEmpresa(empresa);
        }
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);
            notificacion.setUsuario(usuario);
        }

        notificacion.setTipo(dto.getTipo());
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setLeido(false);

        notificacionRepository.save(notificacion);
    }
}
