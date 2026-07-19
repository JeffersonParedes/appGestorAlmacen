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

import java.time.LocalDate;
import java.util.List;
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

        Suscripcion suscripcion = suscripcionMapper.toEntity(dto);
        suscripcion.setEmpresa(empresa);

        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        if ("PAGADO".equals(suscripcion.getEstadoPago()) && suscripcion.getFechaFin().isAfter(LocalDate.now())) {
            empresa.setEstado("ACTIVO");
            empresaRepository.save(empresa);
        }

        return suscripcionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO renovarSuscripcion(Long empresaId, SuscripcionRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Suscripcion suscripcion = suscripcionMapper.toEntity(dto);
        suscripcion.setEmpresa(empresa);
        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        empresa.setEstado("ACTIVO");
        empresaRepository.save(empresa);

        List<Usuario> bodegueros = usuarioRepository.findByEmpresaId(empresaId).stream()
                .filter(u -> "BODEGUERO".equals(u.getRol()))
                .collect(Collectors.toList());

        for (Usuario b : bodegueros) {
            Notificacion notif = new Notificacion();
            notif.setEmpresa(empresa);
            notif.setUsuario(b);
            notif.setTipo("SUSCRIPCION");
            notif.setTitulo("Suscripción Renovada");
            notif.setMensaje("Tu suscripción ha sido renovada hasta el " + guardada.getFechaFin() + ".");
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
