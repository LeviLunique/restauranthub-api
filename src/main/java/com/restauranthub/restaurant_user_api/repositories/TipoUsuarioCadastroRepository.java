package com.restauranthub.restaurant_user_api.repositories;

import com.restauranthub.restaurant_user_api.entities.TipoUsuarioCadastroEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoUsuarioCadastroRepository extends JpaRepository<TipoUsuarioCadastroEntity, Long> {

    Optional<TipoUsuarioCadastroEntity> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
