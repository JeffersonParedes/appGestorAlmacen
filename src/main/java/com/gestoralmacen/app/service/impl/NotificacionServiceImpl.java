package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.mapper.NotificacionMapper;
import com.gestoralmacen.app.repository.NotificacionRepository;
import com.gestoralmacen.app.service.NotificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository, NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    @Override
    @Transactional
    public NotificacionResponseDTO registrarNotificacion(NotificacionRequestDTO dto) {
        Notificacion notificacion = notificacionMapper.toEntity(dto);
        Notificacion guardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));
        notificacion.setLeido(true);
        notificacion.setFechaLectura(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> consultarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId).stream()
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> consultarPorAdministrador(Long adminId) {
        return notificacionRepository.findByAdministradorId(adminId).stream()
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
