package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByEmpresaId(Long empresaId);
    List<Auditoria> findByUsuarioId(Long usuarioId);
}
