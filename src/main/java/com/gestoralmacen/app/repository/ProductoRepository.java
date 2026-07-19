package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEmpresaId(Long empresaId);

    List<Producto> findByEmpresaIdAndEstado(Long empresaId, String estado);

    // Para validar que un empleado no intente crear un código de barras que ya
    // existe en su empresa
    Optional<Producto> findByEmpresaIdAndCodigoBarras(Long empresaId, String codigoBarras);
}
