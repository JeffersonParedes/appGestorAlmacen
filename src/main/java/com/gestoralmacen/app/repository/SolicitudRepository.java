package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByEmpresaId(Long empresaId);
    List<Solicitud> findByUsuarioId(Long usuarioId);
}
