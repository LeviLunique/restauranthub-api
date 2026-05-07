package com.restauranthub.restaurant_user_api.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.restauranthub.restaurant_user_api.domain.Endereco;
import com.restauranthub.restaurant_user_api.domain.Usuario;
import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.EnderecoRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new UsuarioMapper(new EnderecoMapper());
    }

    @Test
    void toDomain_deveMapearUsuarioRequestComEndereco() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Fulano")
                .email("fulano@email.com")
                .login("fulano.login")
                .senha("Senha@123")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .telefone("+55 11 99999-9999")
                .enderecos(List.of(EnderecoRequest.builder()
                        .tipoEndereco(TipoEndereco.RESIDENCIAL)
                        .rua("Rua A")
                        .numero("123")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01001-000")
                        .principal(true)
                        .ativo(true)
                        .build()))
                .build();

        Usuario domain = mapper.toDomain(request);

        assertNotNull(domain);
        assertEquals("Fulano", domain.getNome());
        assertEquals("fulano@email.com", domain.getEmail());
        assertEquals(TipoUsuario.CLIENTE, domain.getTipoUsuario());
        assertEquals(1, domain.getEnderecos().size());
        Endereco endereco = domain.getEnderecos().get(0);
        assertEquals(TipoEndereco.RESIDENCIAL, endereco.getTipoEndereco());
        assertTrue(endereco.getPrincipal());
    }

    @Test
    void updateEntityFromDomain_deveAtualizarEAdicionarEnderecos() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(10L);
        entity.setNome("Antigo");
        entity.setEmail("antigo@email.com");
        entity.setLogin("antigo");
        entity.setSenha("SenhaAntiga");
        entity.setTipoUsuario(TipoUsuario.CLIENTE);
        entity.setAtivo(true);

        EnderecoEntity existente = new EnderecoMapper().toEntity(Endereco.create(
                TipoEndereco.RESIDENCIAL,
                "Rua A",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01001-000",
                true,
                true
        ));
        existente.setId(1L);
        existente.setUsuario(entity);
        entity.getEnderecos().add(existente);

        Usuario domain = new Usuario();
        domain.setNome("Novo Nome");
        domain.setEmail("novo@email.com");
        domain.setLogin("novo.login");
        domain.setSenha("NovaSenha@123");
        domain.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);
        domain.setAtivo(true);
        domain.setEnderecos(List.of(
                Endereco.builder()
                        .id(1L)
                        .tipoEndereco(TipoEndereco.RESIDENCIAL)
                        .rua("Rua Atualizada")
                        .numero("321")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01001-000")
                        .principal(true)
                        .ativo(true)
                        .build(),
                Endereco.builder()
                        .id(null)
                        .tipoEndereco(TipoEndereco.COMERCIAL)
                        .rua("Av. Nova")
                        .numero("999")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01310-100")
                        .principal(false)
                        .ativo(true)
                        .build()
        ));

        mapper.updateEntityFromDomain(domain, entity);

        assertEquals("Novo Nome", entity.getNome());
        assertEquals("novo@email.com", entity.getEmail());
        assertEquals(TipoUsuario.DONO_RESTAURANTE, entity.getTipoUsuario());
        assertEquals(2, entity.getEnderecos().size());
        assertTrue(entity.getEnderecos().stream().anyMatch(e -> "Rua Atualizada".equals(e.getRua())));
        EnderecoEntity novo = entity.getEnderecos().stream()
                .filter(e -> e.getId() == null)
                .findFirst()
                .orElse(null);
        assertNotNull(novo);
        assertEquals(entity, novo.getUsuario());
    }

    @Test
    void toResponse_deveConverterInstantesParaOffsetDateTime() {
        Instant created = Instant.parse("2025-01-01T10:00:00Z");
        Instant updated = Instant.parse("2025-01-02T11:00:00Z");

        Usuario domain = new Usuario();
        domain.setId(1L);
        domain.setNome("Fulano");
        domain.setEmail("fulano@email.com");
        domain.setLogin("fulano.login");
        domain.setTipoUsuario(TipoUsuario.CLIENTE);
        domain.setAtivo(true);
        domain.setCreatedAt(created);
        domain.setUpdatedAt(updated);
        domain.setEnderecos(List.of());

        UsuarioResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(OffsetDateTime.ofInstant(created, ZoneOffset.UTC), response.getDataCriacao());
        assertEquals(OffsetDateTime.ofInstant(updated, ZoneOffset.UTC), response.getDataAtualizacao());
        assertTrue(response.getEnderecos().isEmpty());
    }

    @Test
    void toResponse_deveRetornarNullQuandoDominioForNull() {
        assertNull(mapper.toResponse(null));
    }
}
