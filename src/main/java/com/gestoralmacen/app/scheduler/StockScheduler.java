package com.gestoralmacen.app.scheduler;

import com.gestoralmacen.app.entity.Inventario;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.notification.NotificationFactory;
import com.gestoralmacen.app.notification.NotificationService;
import com.gestoralmacen.app.repository.InventarioRepository;
import com.gestoralmacen.app.repository.NotificacionRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockScheduler {

    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final NotificationService notificationService;

    public StockScheduler(InventarioRepository inventarioRepository,
                          UsuarioRepository usuarioRepository,
                          NotificacionRepository notificacionRepository,
                          NotificationService notificationService) {
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
        this.notificationService = notificationService;
    }

    // Se ejecuta todos los días a las 2:00 AM
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void revisarStockMinimo() {
        System.out.println("⏰ [Scheduler] Iniciando revisión de stock mínimo...");
        List<Inventario> inventarios = inventarioRepository.findAll();

        for (Inventario inv : inventarios) {
            if (inv.getStockActual().compareTo(inv.getStockMinimo()) <= 0) {
                List<Usuario> bodegueros = usuarioRepository.findByEmpresaId(inv.getEmpresa().getId()).stream()
                        .filter(u -> "BODEGUERO".equalsIgnoreCase(u.getRol()))
                        .collect(Collectors.toList());

                for (Usuario bodeguero : bodegueros) {
                    String msg = "El producto " + inv.getProducto().getNombre() + " en almacén " 
                            + inv.getAlmacen().getNombre() + " ha superado el stock mínimo (Actual: " 
                            + inv.getStockActual() + ", Mínimo: " + inv.getStockMinimo() + ").";

                    boolean yaNotificado = notificacionRepository.findByUsuarioId(bodeguero.getId()).stream()
                            .anyMatch(n -> (n.getLeido() == null || !n.getLeido()) && "STOCK".equals(n.getTipo()) && n.getMensaje().equals(msg));

                    if (!yaNotificado) {
                        Notificacion notif = NotificationFactory.crearStockMinimo(inv.getEmpresa(), bodeguero, msg);
                        notificationService.enviarYGuardar(notif);
                    }
                }
            }
        }
        System.out.println("⏰ [Scheduler] Revisión de stock mínimo finalizada.");
    }
}
