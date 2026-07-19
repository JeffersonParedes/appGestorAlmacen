package com.gestoralmacen.app.notification;

import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.mapper.NotificacionMapper;
import com.gestoralmacen.app.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    public NotificationServiceImpl(NotificacionRepository notificacionRepository, NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    @Override
    @Transactional
    public void enviarYGuardar(Notificacion notificacion) {
        notificacionRepository.save(notificacion);
        System.out.println("🔔 Notificación generada: [" + notificacion.getTipo() + "] " + notificacion.getMensaje());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarActivasPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId).stream()
                .filter(n -> n.getLeido() == null || !n.getLeido())
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        Notificacion n = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        n.setLeido(true);
        notificacionRepository.save(n);
    }
}
