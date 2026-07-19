package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    // Para saber cuánto stock hay de todos los productos en una empresa
    List<Inventario> findByEmpresaId(Long empresaId);

    // Para actualizar el stock exacto cuando alguien hace un movimiento en un
    // almacén específico
    Optional<Inventario> findByEmpresaIdAndProductoIdAndAlmacenId(Long empresaId, Long productoId, Long almacenId);

    List<Inventario> findByAlmacenId(Long almacenId);
}