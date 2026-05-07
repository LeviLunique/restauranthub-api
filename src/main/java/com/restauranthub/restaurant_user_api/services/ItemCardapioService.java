package com.restauranthub.restaurant_user_api.services;

import com.restauranthub.restaurant_user_api.dto.ItemCardapioRequest;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioResponse;
import java.util.List;

public interface ItemCardapioService {

    ItemCardapioResponse create(Long restauranteId, ItemCardapioRequest request);

    ItemCardapioResponse update(Long id, ItemCardapioRequest request);

    ItemCardapioResponse findById(Long id);

    List<ItemCardapioResponse> findByRestaurante(Long restauranteId);

    void delete(Long id);
}
