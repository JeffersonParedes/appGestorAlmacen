package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Trae solo las categorías de la empresa que está logueada
    List<Categoria> findByEmpresaId(Long empresaId);

    // Trae las categorías de la empresa filtrando por estado (ej: para la papelera
    // que mencionaste)
    List<Categoria> findByEmpresaIdAndEstado(Long empresaId, String estado);

    java.util.Optional<Categoria> findByEmpresaIdAndNombre(Long empresaId, String nombre);
}