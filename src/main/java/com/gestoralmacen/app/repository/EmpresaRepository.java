package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Útil para que ustedes como Admin busquen a un cliente por su RUC
    Optional<Empresa> findByRuc(String ruc);
}
