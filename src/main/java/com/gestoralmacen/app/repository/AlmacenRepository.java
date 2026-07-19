package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    List<Almacen> findByEmpresaId(Long empresaId);
}
