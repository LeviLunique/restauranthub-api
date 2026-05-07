package com.restauranthub.restaurant_user_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.entities.TipoUsuarioCadastroEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.mappers.EnderecoMapper;
import com.restauranthub.restaurant_user_api.mappers.TipoUsuarioCadastroMapper;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.repositories.TipoUsuarioCadastroRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.impl.TipoUsuarioCadastroServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class TipoUsuarioCadastroServiceImplTest {

    @Mock
    private TipoUsuarioCadastroRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private TipoUsuarioCadastroServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TipoUsuarioCadastroServiceImpl(
                repository,
                usuarioRepository,
                new TipoUsuarioCadastroMapper(),
                new UsuarioMapper(new EnderecoMapper()));
    }

    @Test
    void create_deveCadastrarTipoSuportado() {
        when(repository.findByNomeIgnoreCase("FUNCIONARIO")).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(TipoUsuarioCadastroEntity.class))).thenAnswer(invocation -> {
            TipoUsuarioCadastroEntity entity = invocation.getArgument(0);
            entity.setId(4L);
            return entity;
        });

        var response = service.create(TipoUsuarioCadastroRequest.builder()
                .nome("funcionario")
                .descricao("Equipe interna")
                .build());

        assertEquals("FUNCIONARIO", response.getNome());
    }

    @Test
    void create_deveLancarQuandoTipoNaoForSuportado() {
        assertThrows(DomainValidationException.class, () -> service.create(TipoUsuarioCadastroRequest.builder()
                .nome("PARCEIRO")
                .descricao("Tipo inválido")
                .build()));
    }

    @Test
    void associateToUser_deveSincronizarCatalogoEEnum() {
        TipoUsuarioCadastroEntity tipo = new TipoUsuarioCadastroEntity();
        tipo.setId(2L);
        tipo.setNome("DONO_RESTAURANTE");
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(9L);
        usuario.setNome("Roberto");
        usuario.setEmail("roberto@email.com");
        usuario.setLogin("roberto");
        usuario.setSenha("secret");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setAtivo(true);

        when(repository.findById(2L)).thenReturn(Optional.of(tipo));
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse response = service.associateToUser(2L, 9L);

        assertEquals(TipoUsuario.DONO_RESTAURANTE, response.getTipoUsuario());
        assertEquals("DONO_RESTAURANTE", response.getTipoUsuarioNome());
    }
}
