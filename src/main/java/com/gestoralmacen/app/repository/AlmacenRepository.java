package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    List<Almacen> findByEmpresaId(Long empresaId);

    // Validaciones de duplicados por empresa
    boolean existsByEmpresaIdAndNombreIgnoreCase(Long empresaId, String nombre);
    boolean existsByEmpresaIdAndDireccionIgnoreCase(Long empresaId, String direccion);
    boolean existsByEmpresaIdAndNombreIgnoreCaseAndIdNot(Long empresaId, String nombre, Long id);
    boolean existsByEmpresaIdAndDireccionIgnoreCaseAndIdNot(Long empresaId, String direccion, Long id);
}
