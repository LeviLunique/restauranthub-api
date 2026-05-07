package com.restauranthub.restaurant_user_api.repositories;

import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<RestauranteEntity, Long> {

    List<RestauranteEntity> findByDonoId(Long donoId);
}
