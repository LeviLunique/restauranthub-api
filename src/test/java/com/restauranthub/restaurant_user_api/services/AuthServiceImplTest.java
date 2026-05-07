package com.restauranthub.restaurant_user_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.restauranthub.restaurant_user_api.config.security.JwtService;
import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.LoginRequest;
import com.restauranthub.restaurant_user_api.dto.LoginResponse;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.mappers.EnderecoMapper;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.impl.AuthServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

class AuthServiceImplTest {

    @Mock
    private UsuarioRepository repository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new AuthServiceImpl(repository, new UsuarioMapper(new EnderecoMapper()), jwtService, authenticationManager);
    }

    @Test
    void login_deveGerarTokenParaUsuarioAtivo() {
        UsuarioEntity entity = usuarioAtivo();
        when(repository.findByEmailIgnoreCase(entity.getEmail())).thenReturn(Optional.of(entity));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                new User(entity.getEmail(), entity.getSenha(), java.util.List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))),
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(jwtService.generateToken(any())).thenReturn("fake-token");

        LoginResponse response = service.login(LoginRequest.builder()
                .email(entity.getEmail())
                .senha("Senha@123")
                .build());

        assertEquals("fake-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(entity.getEmail(), response.getUsuario().getEmail());
    }

    @Test
    void login_deveFalharQuandoUsuarioInativo() {
        UsuarioEntity entity = usuarioAtivo();
        entity.setAtivo(false);
        when(repository.findByEmailIgnoreCase(entity.getEmail())).thenReturn(Optional.of(entity));

        assertThrows(DomainValidationException.class, () -> service.login(LoginRequest.builder()
                .email(entity.getEmail())
                .senha("Senha@123")
                .build()));
    }

    @Test
    void login_deveFalharQuandoCredenciaisInvalidas() {
        UsuarioEntity entity = usuarioAtivo();
        when(repository.findByEmailIgnoreCase(entity.getEmail())).thenReturn(Optional.of(entity));
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("invalid"));

        assertThrows(DomainValidationException.class, () -> service.login(LoginRequest.builder()
                .email(entity.getEmail())
                .senha("Errada")
                .build()));
    }

    @Test
    void login_deveFalharQuandoUsuarioNaoEncontrado() {
        when(repository.findByEmailIgnoreCase("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.login(LoginRequest.builder()
                .email("naoexiste@email.com")
                .senha("Senha@123")
                .build()));
    }

    private UsuarioEntity usuarioAtivo() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1L);
        entity.setNome("Fulano");
        entity.setEmail("fulano@email.com");
        entity.setLogin("fulano.login");
        entity.setSenha("Senha@123");
        entity.setTipoUsuario(TipoUsuario.CLIENTE);
        entity.setAtivo(true);

        EnderecoEntity end = new EnderecoEntity();
        end.setId(1L);
        end.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        end.setRua("Rua A");
        end.setNumero("123");
        end.setCidade("São Paulo");
        end.setEstado("SP");
        end.setCep("01001-000");
        end.setPrincipal(true);
        end.setAtivo(true);
        end.setUsuario(entity);
        entity.getEnderecos().add(end);
        return entity;
    }
}
