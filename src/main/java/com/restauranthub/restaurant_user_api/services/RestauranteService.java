package com.restauranthub.restaurant_user_api.services;

import com.restauranthub.restaurant_user_api.dto.RestauranteRequest;
import com.restauranthub.restaurant_user_api.dto.RestauranteResponse;
import java.util.List;

public interface RestauranteService {

    RestauranteResponse create(RestauranteRequest request);

    RestauranteResponse update(Long id, RestauranteRequest request);

    RestauranteResponse findById(Long id);

    List<RestauranteResponse> findAll();

    void delete(Long id);
}
