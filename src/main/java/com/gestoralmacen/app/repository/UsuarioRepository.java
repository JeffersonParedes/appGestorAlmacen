package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Súper importante para el Login (Spring Security)
    Optional<Usuario> findByUsuario(String usuario);

    // Para que el dueño vea la lista de sus empleados
    List<Usuario> findByEmpresaId(Long empresaId);
}