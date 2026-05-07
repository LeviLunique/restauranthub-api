package com.restauranthub.restaurant_user_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.restauranthub.restaurant_user_api.domain.Usuario;
import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.AlterarSenhaRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.EnderecoMapper;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.repositories.TipoUsuarioCadastroRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.impl.UsuarioServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private TipoUsuarioCadastroRepository tipoUsuarioCadastroRepository;

    private UsuarioServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(tipoUsuarioCadastroRepository.findByNomeIgnoreCase(any())).thenReturn(Optional.empty());
        service = new UsuarioServiceImpl(repository, tipoUsuarioCadastroRepository, new UsuarioMapper(new EnderecoMapper()));
    }

    @Test
    void create_devePersistirUsuarioComSenhaCriptografada() {
        UsuarioRequest request = TestData.novoUsuarioRequest();

        when(repository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.empty());
        when(repository.findByLoginIgnoreCase(request.getLogin())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> {
            UsuarioEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            entity.getEnderecos().forEach(e -> e.setId(100L));
            return entity;
        });

        UsuarioResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        UsuarioEntity salvo = captor.getValue();
        assertFalse("Senha@123".equals(salvo.getSenha()), "Senha deve estar criptografada");
    }

    @Test
    void create_deveLancarExcecaoQuandoEmailDuplicado() {
        UsuarioRequest request = TestData.novoUsuarioRequest();
        UsuarioEntity existente = new UsuarioEntity();
        existente.setId(99L);
        when(repository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(existente));

        assertThrows(DomainValidationException.class, () -> service.create(request));
    }

    @Test
    void update_deveAtualizarDadosBasicos() {
        UsuarioEntity existente = TestData.usuarioEntityPersistido();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByEmailIgnoreCase("novo@email.com")).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioUpdateRequest update = UsuarioUpdateRequest.builder()
                .nome("Nome Atualizado")
                .email("novo@email.com")
                .telefone("+55 11 90000-0000")
                .tipoUsuario(TipoUsuario.ADMIN)
                .enderecos(List.of(TestData.novoEnderecoRequest()))
                .ativo(true)
                .build();

        UsuarioResponse response = service.update(1L, update);

        assertEquals("Nome Atualizado", response.getNome());
        assertEquals("novo@email.com", response.getEmail());
        assertEquals(TipoUsuario.ADMIN, response.getTipoUsuario());
    }

    @Test
    void update_devePermitirMesmoEmailDoUsuarioComCaseEDelimitadoresDiferentes() {
        UsuarioEntity existente = TestData.usuarioEntityPersistido();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioUpdateRequest update = UsuarioUpdateRequest.builder()
                .email("  FULANO@EMAIL.COM  ")
                .build();

        UsuarioResponse response = service.update(1L, update);

        assertEquals("fulano@email.com", response.getEmail());
    }

    @Test
    void update_deveLancarQuandoNaoEncontrarUsuario() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, UsuarioUpdateRequest.builder().build()));
    }

    @Test
    void alterarSenha_deveAtualizarSenhaQuandoAtualCorreta() {
        UsuarioEntity existente = TestData.usuarioEntityPersistido();
        existente.setSenha(new BCryptPasswordEncoder().encode("Senha@123"));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlterarSenhaRequest request = AlterarSenhaRequest.builder()
                .senhaAtual("Senha@123")
                .novaSenha("NovaSenha@456")
                .build();

        service.alterarSenha(1L, request);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        UsuarioEntity salvo = captor.getValue();
        assertFalse(new BCryptPasswordEncoder().matches("Senha@123", salvo.getSenha()), "Senha deve ser nova");
    }

    @Test
    void alterarSenha_deveLancarQuandoSenhaAtualInvalida() {
        UsuarioEntity existente = TestData.usuarioEntityPersistido();
        existente.setSenha(new BCryptPasswordEncoder().encode("Senha@123"));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        AlterarSenhaRequest request = AlterarSenhaRequest.builder()
                .senhaAtual("Errada")
                .novaSenha("NovaSenha@456")
                .build();

        assertThrows(DomainValidationException.class, () -> service.alterarSenha(1L, request));
    }

    @Test
    void desativar_deveMarcarUsuarioComoInativo() {
        UsuarioEntity existente = TestData.usuarioEntityPersistido();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.desativar(1L);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        assertFalse(captor.getValue().getAtivo());
    }

    @Test
    void findAll_deveConverterPagina() {
        UsuarioEntity entity = TestData.usuarioEntityPersistido();
        when(repository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(entity)));

        var response = service.findAll(0, 2, null);

        assertEquals(1, response.getContent().size());
        assertEquals(entity.getNome(), response.getContent().get(0).getNome());
    }

    private static class TestData {
        static UsuarioRequest novoUsuarioRequest() {
            return UsuarioRequest.builder()
                    .nome("Fulano")
                    .email("fulano@email.com")
                    .login("fulano.login")
                    .senha("Senha@123")
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .telefone("+55 11 99999-9999")
                    .enderecos(List.of(novoEnderecoRequest()))
                    .build();
        }

        static com.restauranthub.restaurant_user_api.dto.EnderecoRequest novoEnderecoRequest() {
            return com.restauranthub.restaurant_user_api.dto.EnderecoRequest.builder()
                    .tipoEndereco(TipoEndereco.RESIDENCIAL)
                    .rua("Rua A")
                    .numero("123")
                    .cidade("São Paulo")
                    .estado("SP")
                    .cep("01001-000")
                    .principal(true)
                    .ativo(true)
                    .build();
        }

        static UsuarioEntity usuarioEntityPersistido() {
            UsuarioEntity entity = new UsuarioEntity();
            entity.setId(1L);
            entity.setNome("Fulano");
            entity.setEmail("fulano@email.com");
            entity.setLogin("fulano.login");
            entity.setSenha("Senha@123");
            entity.setTipoUsuario(TipoUsuario.CLIENTE);
            entity.setAtivo(true);

            EnderecoEntity end = new EnderecoEntity();
            end.setId(100L);
            end.setRua("Rua A");
            end.setNumero("123");
            end.setCidade("São Paulo");
            end.setEstado("SP");
            end.setCep("01001-000");
            end.setTipoEndereco(TipoEndereco.RESIDENCIAL);
            end.setPrincipal(true);
            end.setAtivo(true);
            end.setUsuario(entity);
            entity.getEnderecos().add(end);
            return entity;
        }
    }
}
