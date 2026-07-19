package com.gestoralmacen.app.scheduler;

import com.gestoralmacen.app.service.SuscripcionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SuscripcionScheduler {

    private final SuscripcionService suscripcionService;

    public SuscripcionScheduler(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    // Se ejecuta todos los días a la 1:00 AM
    @Scheduled(cron = "0 0 1 * * *")
    public void revisarSuscripcionesVencidas() {
        System.out.println("⏰ [Scheduler] Iniciando revisión de suscripciones vencidas...");
        suscripcionService.actualizarEstadoSuscripciones();
        System.out.println("⏰ [Scheduler] Revisión de suscripciones vencidas finalizada.");
    }
}
