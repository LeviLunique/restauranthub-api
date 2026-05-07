package com.restauranthub.restaurant_user_api.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.ApiMessageResponse;
import com.restauranthub.restaurant_user_api.dto.EnderecoRequest;
import com.restauranthub.restaurant_user_api.dto.PageMetadata;
import com.restauranthub.restaurant_user_api.dto.PageResponse;
import com.restauranthub.restaurant_user_api.dto.SortResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;
import com.restauranthub.restaurant_user_api.services.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    // Evita falha de metamodel JPA ao carregar contexto do @EnableJpaAuditing
    @MockBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void postUsers_deveCriarUsuario() throws Exception {
        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Fulano")
                .email("fulano@email.com")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();
        when(usuarioService.create(any())).thenReturn(response);

        UsuarioRequest request = UsuarioRequest.builder()
                .nome("Fulano")
                .email("fulano@email.com")
                .login("fulano.login")
                .senha("Senha@123")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .enderecos(List.of(EnderecoRequest.builder()
                        .tipoEndereco(com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco.RESIDENCIAL)
                        .rua("Rua A")
                        .numero("123")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01001-000")
                        .principal(true)
                        .build()))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.nome", equalTo("Fulano")));
    }

    @Test
    void postUsers_deveValidarCamposObrigatorios() throws Exception {
        UsuarioRequest request = UsuarioRequest.builder()
                .email("invalido")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_deveRetornarPaginado() throws Exception {
        UsuarioResumoResponse resumo = UsuarioResumoResponse.builder()
                .id(1L)
                .nome("Fulano")
                .email("fulano@email.com")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .ativo(true)
                .build();
        PageResponse<UsuarioResumoResponse> page = new PageResponse<>(
                List.of(resumo),
                PageMetadata.builder()
                        .pageNumber(0)
                        .pageSize(10)
                        .sort(SortResponse.builder().sorted(true).unsorted(false).empty(false).build())
                        .build(),
                1,
                1
        );
        when(usuarioService.findAll(0, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/v1/users").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", equalTo(1)));
    }

    @Test
    void putUsers_deveAtualizarUsuario() throws Exception {
        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nome("Atualizado")
                .email("novo@email.com")
                .tipoUsuario(TipoUsuario.CLIENTE)
                .build();
        when(usuarioService.update(any(), any())).thenReturn(response);

        UsuarioUpdateRequest update = UsuarioUpdateRequest.builder()
                .nome("Atualizado")
                .email("novo@email.com")
                .build();

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", equalTo("Atualizado")));
    }

    @Test
    void patchPassword_deveAlterarSenha() throws Exception {
        when(usuarioService.alterarSenha(any(), any())).thenReturn(ApiMessageResponse.builder()
                .message("Senha alterada com sucesso")
                .build());

        mockMvc.perform(patch("/api/v1/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senhaAtual":"Senha@123","novaSenha":"NovaSenha@456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Senha alterada com sucesso")));
    }

    @Test
    void deleteUser_deveResponderNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
