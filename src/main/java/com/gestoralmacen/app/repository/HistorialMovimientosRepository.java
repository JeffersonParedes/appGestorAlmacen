package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.HistorialMovimientos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialMovimientosRepository extends JpaRepository<HistorialMovimientos, Long> {
    // Para sacar el reporte Kardex de la empresa
    List<HistorialMovimientos> findByEmpresaId(Long empresaId);

    // Para ver los movimientos de un producto en específico
    List<HistorialMovimientos> findByEmpresaIdAndProductoIdOrderByFechaMovimientoDesc(Long empresaId, Long productoId);
}
