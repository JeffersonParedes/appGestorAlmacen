package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    // Búsqueda por RUC
    Optional<Empresa> findByRuc(String ruc);

    // Verificaciones de existencia / duplicados para el Registro SaaS
    boolean existsByRuc(String ruc);
    boolean existsByCorreoContacto(String correoContacto);
    boolean existsByTelefonoContacto(String telefonoContacto);
    boolean existsByDireccionPrincipal(String direccionPrincipal);
}
