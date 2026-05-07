package com.restauranthub.restaurant_user_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.RestauranteRequest;
import com.restauranthub.restaurant_user_api.dto.RestauranteResponse;
import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.mappers.RestauranteMapper;
import com.restauranthub.restaurant_user_api.repositories.RestauranteRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.impl.RestauranteServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RestauranteServiceImplTest {

    @Mock
    private RestauranteRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private RestauranteServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new RestauranteServiceImpl(repository, usuarioRepository, new RestauranteMapper());
    }

    @Test
    void create_devePersistirRestauranteQuandoDonoValido() {
        UsuarioEntity dono = donoAtivo();
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(dono));
        when(repository.saveAndFlush(any(RestauranteEntity.class))).thenAnswer(invocation -> {
            RestauranteEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        RestauranteResponse response = service.create(restauranteRequest());

        assertEquals(1L, response.getId());
        assertEquals("Cantina Teste", response.getNome());
        assertEquals(10L, response.getDonoUsuarioId());
    }

    @Test
    void create_deveLancarQuandoUsuarioNaoForDono() {
        UsuarioEntity usuario = donoAtivo();
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        assertThrows(DomainValidationException.class, () -> service.create(restauranteRequest()));
    }

    private RestauranteRequest restauranteRequest() {
        return RestauranteRequest.builder()
                .nome("Cantina Teste")
                .tipoCozinha("Italiana")
                .horarioFuncionamento("Seg-Sex 11h-22h")
                .donoUsuarioId(10L)
                .rua("Rua A")
                .numero("10")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01001-000")
                .build();
    }

    private UsuarioEntity donoAtivo() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(10L);
        usuario.setNome("Roberto Faria");
        usuario.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);
        usuario.setAtivo(true);
        return usuario;
    }
}
