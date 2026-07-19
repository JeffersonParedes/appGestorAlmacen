package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByEmpresaId(Long empresaId);
    List<Notificacion> findByUsuarioId(Long usuarioId);
    List<Notificacion> findByAdministradorId(Long administradorId);
}
