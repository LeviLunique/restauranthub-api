package com.restauranthub.restaurant_user_api.repositories;

import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {
    List<EnderecoEntity> findByUsuarioId(Long usuarioId);
}
