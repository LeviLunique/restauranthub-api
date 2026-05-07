package com.restauranthub.restaurant_user_api.repositories;

import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    Optional<UsuarioEntity> findByLoginIgnoreCase(String login);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCase(String login);

    Page<UsuarioEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<UsuarioEntity> findByTipoUsuario(TipoUsuario tipoUsuario, Pageable pageable);
}
