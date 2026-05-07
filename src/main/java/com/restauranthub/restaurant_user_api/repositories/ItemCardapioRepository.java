package com.restauranthub.restaurant_user_api.repositories;

import com.restauranthub.restaurant_user_api.entities.ItemCardapioEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCardapioRepository extends JpaRepository<ItemCardapioEntity, Long> {

    List<ItemCardapioEntity> findByRestauranteId(Long restauranteId);
}
