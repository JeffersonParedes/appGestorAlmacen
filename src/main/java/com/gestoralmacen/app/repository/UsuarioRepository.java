package com.gestoralmacen.app.repository;

import com.gestoralmacen.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Fetch join para cargar usuario y empresa en una sola consulta
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.usuario = :usuario")
    Optional<Usuario> findByUsuario(@Param("usuario") String usuario);

    // Para que el dueño vea la lista de sus empleados
    List<Usuario> findByEmpresaId(Long empresaId);
}