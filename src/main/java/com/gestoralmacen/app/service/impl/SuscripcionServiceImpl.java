package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.SuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.entity.Suscripcion;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.SuscripcionMapper;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.NotificacionRepository;
import com.gestoralmacen.app.repository.SuscripcionRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.service.SuscripcionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuscripcionServiceImpl implements SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final SuscripcionMapper suscripcionMapper;
    private final EmpresaRepository empresaRepository;
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public SuscripcionServiceImpl(SuscripcionRepository suscripcionRepository,
                                  SuscripcionMapper suscripcionMapper,
                                  EmpresaRepository empresaRepository,
                                  NotificacionRepository notificacionRepository,
                                  UsuarioRepository usuarioRepository) {
        this.suscripcionRepository = suscripcionRepository;
        this.suscripcionMapper = suscripcionMapper;
        this.empresaRepository = empresaRepository;
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO registrarSuscripcion(SuscripcionRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        // Normalización del plan
        String planReq = dto.getPlanSuscripcion() != null ? dto.getPlanSuscripcion().toUpperCase() : "BASICO";
        String planFinal = "BASICO";
        BigDecimal montoFinal = new BigDecimal("15.00");
        int meses = 1;

        if (planReq.contains("PREMIUM")) {
            planFinal = "PREMIUM";
            montoFinal = new BigDecimal("150.00");
            meses = 12;
        } else if (planReq.contains("PRO")) {
            planFinal = "PRO";
            montoFinal = new BigDecimal("85.00");
            meses = 6;
        } else {
            planFinal = "BASICO";
            montoFinal = new BigDecimal("15.00");
            meses = 1;
        }

        LocalDate inicio = dto.getFechaInicio() != null ? dto.getFechaInicio() : LocalDate.now();
        LocalDate fin = inicio.plusMonths(meses);

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setEmpresa(empresa);
        suscripcion.setPlanSuscripcion(planFinal);
        suscripcion.setTipoSuscripcion(dto.getTipoSuscripcion() != null ? dto.getTipoSuscripcion() : "PRIMER_REGISTRO");
        suscripcion.setFechaInicio(inicio);
        suscripcion.setFechaFin(fin);
        suscripcion.setMontoPagado(montoFinal);
        suscripcion.setEstadoPago("PAGADO");
        suscripcion.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "TRANSFERENCIA");

        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        empresa.setEstado("ACTIVO");
        empresaRepository.save(empresa);

        return suscripcionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO renovarSuscripcion(Long empresaId, SuscripcionRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        // 1. Obtener la suscripción más reciente de la empresa
        List<Suscripcion> suscripcionesPrevias = suscripcionRepository.findByEmpresaId(empresaId);
        Optional<Suscripcion> ultimaSuscripcionOpt = suscripcionesPrevias.stream()
                .max(Comparator.comparing(Suscripcion::getFechaFin));

        LocalDate hoy = LocalDate.now();
        Suscripcion suscripcionTarget;

        if (ultimaSuscripcionOpt.isPresent()) {
            suscripcionTarget = ultimaSuscripcionOpt.get();
            LocalDate fechaFinActual = suscripcionTarget.getFechaFin();
            LocalDate fechaPermitidaRenovacion = fechaFinActual.minusMonths(1);

            // Regla de Negocio: Únicamente renovar cuando falte 1 mes o menos para vencer
            if (hoy.isBefore(fechaPermitidaRenovacion)) {
                String fechaFormateada = fechaPermitidaRenovacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                throw new ReglaNegocioException("No es posible realizar una renovación todavía.\nLa suscripción podrá renovarse a partir del: " + fechaFormateada + ".");
            }
        } else {
            suscripcionTarget = new Suscripcion();
            suscripcionTarget.setEmpresa(empresa);
        }

        // 2. Determinar plan y valores automáticos
        String planReq = dto.getPlanSuscripcion() != null ? dto.getPlanSuscripcion().toUpperCase() : "BASICO";
        String planFinal = "BASICO";
        BigDecimal montoFinal = new BigDecimal("15.00");
        int meses = 1;

        if (planReq.contains("PREMIUM")) {
            planFinal = "PREMIUM";
            montoFinal = new BigDecimal("150.00");
            meses = 12;
        } else if (planReq.contains("PRO")) {
            planFinal = "PRO";
            montoFinal = new BigDecimal("85.00");
            meses = 6;
        } else {
            planFinal = "BASICO";
            montoFinal = new BigDecimal("15.00");
            meses = 1;
        }

        LocalDate fechaInicio;
        if (ultimaSuscripcionOpt.isPresent() && ultimaSuscripcionOpt.get().getFechaFin().isAfter(hoy)) {
            fechaInicio = ultimaSuscripcionOpt.get().getFechaFin();
        } else {
            fechaInicio = hoy;
        }
        LocalDate fechaFin = fechaInicio.plusMonths(meses);

        // Actualizar la suscripción existente en la base de datos (no crear una nueva)
        suscripcionTarget.setPlanSuscripcion(planFinal);
        suscripcionTarget.setTipoSuscripcion("RENOVACION");
        suscripcionTarget.setFechaInicio(fechaInicio);
        suscripcionTarget.setFechaFin(fechaFin);
        suscripcionTarget.setMontoPagado(montoFinal);
        suscripcionTarget.setEstadoPago("PAGADO");
        suscripcionTarget.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "TRANSFERENCIA");

        Suscripcion guardada = suscripcionRepository.save(suscripcionTarget);

        empresa.setEstado("ACTIVO");
        empresaRepository.save(empresa);

        // Notificar a los Bodegueros
        List<Usuario> bodegueros = usuarioRepository.findByEmpresaId(empresaId).stream()
                .filter(u -> "BODEGUERO".equals(u.getRol()))
                .collect(Collectors.toList());

        for (Usuario b : bodegueros) {
            Notificacion notif = new Notificacion();
            notif.setEmpresa(empresa);
            notif.setUsuario(b);
            notif.setTipo("SUSCRIPCION");
            notif.setTitulo("Suscripción Renovada (" + planFinal + ")");
            notif.setMensaje("Tu suscripción ha sido renovada hasta el " + guardada.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            notificacionRepository.save(notif);
        }

        return suscripcionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> consultarSuscripcionesPorVencer(int diasThreshold) {
        LocalDate limitDate = LocalDate.now().plusDays(diasThreshold);
        return suscripcionRepository.findAll().stream()
                .filter(s -> s.getFechaFin().isAfter(LocalDate.now()) && s.getFechaFin().isBefore(limitDate))
                .map(suscripcionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> consultarHistorial(Long empresaId) {
        return suscripcionRepository.findByEmpresaId(empresaId).stream()
                .map(suscripcionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarEstadoSuscripciones() {
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Suscripcion s : suscripciones) {
            Empresa empresa = s.getEmpresa();

            if (s.getFechaFin().isBefore(hoy) && !"VENCIDO".equals(s.getEstadoPago())) {
                s.setEstadoPago("VENCIDO");
                suscripcionRepository.save(s);

                if ("ACTIVO".equals(empresa.getEstado())) {
                    empresa.setEstado("SUSPENDIDO");
                    empresaRepository.save(empresa);

                    List<Usuario> bodegueros = usuarioRepository.findByEmpresaId(empresa.getId()).stream()
                            .filter(u -> "BODEGUERO".equals(u.getRol()))
                            .collect(Collectors.toList());

                    for (Usuario b : bodegueros) {
                        Notificacion notif = new Notificacion();
                        notif.setEmpresa(empresa);
                        notif.setUsuario(b);
                        notif.setTipo("SUSCRIPCION");
                        notif.setTitulo("Suscripción Vencida - Acceso Suspendido");
                        notif.setMensaje("Tu suscripción venció el " + s.getFechaFin() + ". El acceso ha sido suspendido.");
                        notificacionRepository.save(notif);
                    }
                }
            }
        }
    }
}
