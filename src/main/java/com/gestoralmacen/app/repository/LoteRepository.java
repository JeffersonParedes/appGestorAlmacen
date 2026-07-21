package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByProductoId(Long productoId);
    Optional<Lote> findByProductoIdAndNumeroLote(Long productoId, String numeroLote);
    List<Lote> findByEmpresaId(Long empresaId);

    // Verificación de número de lote único por empresa
    boolean existsByEmpresaIdAndNumeroLote(Long empresaId, String numeroLote);
}
