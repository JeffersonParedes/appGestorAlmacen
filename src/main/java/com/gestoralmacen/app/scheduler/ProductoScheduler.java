package com.gestoralmacen.app.scheduler;

import com.gestoralmacen.app.entity.Lote;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.notification.NotificationFactory;
import com.gestoralmacen.app.notification.NotificationService;
import com.gestoralmacen.app.repository.LoteRepository;
import com.gestoralmacen.app.repository.NotificacionRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoScheduler {

    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final NotificationService notificationService;

    public ProductoScheduler(LoteRepository loteRepository,
                              UsuarioRepository usuarioRepository,
                              NotificacionRepository notificacionRepository,
                              NotificationService notificationService) {
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
        this.notificationService = notificationService;
    }

    // Se ejecuta todos los días a las 3:00 AM
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void revisarProductosProximosAVencer() {
        System.out.println("⏰ [Scheduler] Iniciando revisión de productos próximos a vencer...");
        LocalDate limitDate = LocalDate.now().plusDays(30);
        List<Lote> lotes = loteRepository.findAll();

        for (Lote lote : lotes) {
            if (lote.getFechaVencimiento() != null && 
                !lote.getFechaVencimiento().isBefore(LocalDate.now()) && 
                lote.getFechaVencimiento().isBefore(limitDate)) {

                List<Usuario> bodegueros = usuarioRepository.findByEmpresaId(lote.getEmpresa().getId()).stream()
                        .filter(u -> "BODEGUERO".equalsIgnoreCase(u.getRol()))
                        .collect(Collectors.toList());

                for (Usuario bodeguero : bodegueros) {
                    String msg = "El lote " + lote.getNumeroLote() + " del producto " 
                            + lote.getProducto().getNombre() + " vencerá el " + lote.getFechaVencimiento() + ".";

                    boolean yaNotificado = notificacionRepository.findByUsuarioId(bodeguero.getId()).stream()
                            .anyMatch(n -> (n.getLeido() == null || !n.getLeido()) && "VENCIMIENTO".equals(n.getTipo()) && n.getMensaje().equals(msg));

                    if (!yaNotificado) {
                        Notificacion notif = NotificationFactory.crearProductoProximoVencer(lote.getEmpresa(), bodeguero, msg);
                        notificationService.enviarYGuardar(notif);
                    }
                }
            }
        }
        System.out.println("⏰ [Scheduler] Revisión de productos próximos a vencer finalizada.");
    }
}
