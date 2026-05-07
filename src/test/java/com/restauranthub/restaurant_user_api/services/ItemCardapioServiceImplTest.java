package com.restauranthub.restaurant_user_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.restauranthub.restaurant_user_api.dto.ItemCardapioRequest;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioResponse;
import com.restauranthub.restaurant_user_api.entities.ItemCardapioEntity;
import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import com.restauranthub.restaurant_user_api.mappers.ItemCardapioMapper;
import com.restauranthub.restaurant_user_api.repositories.ItemCardapioRepository;
import com.restauranthub.restaurant_user_api.repositories.RestauranteRepository;
import com.restauranthub.restaurant_user_api.services.impl.ItemCardapioServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemCardapioServiceImplTest {

    @Mock
    private ItemCardapioRepository repository;

    @Mock
    private RestauranteRepository restauranteRepository;

    private ItemCardapioServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ItemCardapioServiceImpl(repository, restauranteRepository, new ItemCardapioMapper());
    }

    @Test
    void create_devePersistirItemParaRestauranteExistente() {
        RestauranteEntity restaurante = new RestauranteEntity();
        restaurante.setId(20L);
        restaurante.setNome("Cantina");
        when(restauranteRepository.findById(20L)).thenReturn(Optional.of(restaurante));
        when(repository.saveAndFlush(any(ItemCardapioEntity.class))).thenAnswer(invocation -> {
            ItemCardapioEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        ItemCardapioResponse response = service.create(20L, ItemCardapioRequest.builder()
                .nome("Lasanha")
                .descricao("Massa fresca")
                .preco(new BigDecimal("45.90"))
                .apenasNoLocal(false)
                .caminhoFoto("/img/lasanha.jpg")
                .build());

        assertEquals(1L, response.getId());
        assertEquals(20L, response.getRestauranteId());
        assertEquals("Lasanha", response.getNome());
    }
}
