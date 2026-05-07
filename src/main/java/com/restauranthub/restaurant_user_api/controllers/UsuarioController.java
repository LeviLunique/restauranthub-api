package com.restauranthub.restaurant_user_api.controllers;

import com.restauranthub.restaurant_user_api.dto.AlterarSenhaRequest;
import com.restauranthub.restaurant_user_api.dto.ApiMessageResponse;
import com.restauranthub.restaurant_user_api.dto.PageResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;
import com.restauranthub.restaurant_user_api.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1")
@Tag(name = "Usuários", description = "Operações de gestão de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/users")
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário com ao menos um endereço ativo principal.")
    @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UsuarioResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id": 11,
                                      "nome": "Maria da Silva",
                                      "email": "maria@email.com",
                                      "login": "maria.silva",
                                      "tipoUsuario": "CLIENTE",
                                      "telefone": "+55 11 99999-9999",
                                      "enderecos": [
                                        {
                                          "id": 21,
                                          "tipoEndereco": "RESIDENCIAL",
                                          "rua": "Rua das Flores",
                                          "numero": "123",
                                          "cidade": "São Paulo",
                                          "estado": "SP",
                                          "cep": "01001-000",
                                          "principal": true,
                                          "ativo": true
                                        }
                                      ],
                                      "ativo": true
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação de payload ou domínio",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    name = "Payload inválido",
                                    value = """
                                            {
                                              "type": "https://restauranthub.com/problems/validation-error",
                                              "title": "Erro de validação",
                                              "status": 400,
                                              "detail": "Dados inválidos",
                                              "instance": "/api/v1/users",
                                              "errors": {
                                                "email": "email inválido"
                                              }
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Regra de negócio",
                                    value = """
                                            {
                                              "type": "https://restauranthub.com/problems/domain-validation",
                                              "title": "Erro de validação de domínio",
                                              "status": 400,
                                              "detail": "Email já cadastrado",
                                              "instance": "/api/v1/users"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Buscar usuário por id")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "type": "https://restauranthub.com/problems/not-found",
                                      "title": "Recurso não encontrado",
                                      "status": 404,
                                      "detail": "Usuário não encontrado com id: 999",
                                      "instance": "/api/v1/users/999"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/users")
    @Operation(summary = "Listar usuários", description = "Retorna uma lista paginada e ordenável de usuários.")
    public ResponseEntity<PageResponse<UsuarioResumoResponse>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(service.findAll(page, size, sort));
    }

    @GetMapping("/users/search")
    @Operation(summary = "Buscar usuários por nome")
    public ResponseEntity<PageResponse<UsuarioResumoResponse>> buscarPorNome(
            @RequestParam("nome") String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(service.searchByNome(nome, page, size, sort));
    }

    @GetMapping("/users/email/{email}")
    @Operation(summary = "Buscar usuário por email")
    public ResponseEntity<UsuarioResumoResponse> buscarPorEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping("/users/{id}")
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza parcialmente os dados do usuário. O mesmo email do próprio usuário é aceito, mesmo com diferenças de maiúsculas/minúsculas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UsuarioResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "id": 1,
                                      "nome": "Administrador Atualizado",
                                      "email": "admin@restauranthub.com",
                                      "login": "admin",
                                      "tipoUsuario": "ADMIN",
                                      "telefone": "+55 11 98888-7777",
                                      "enderecos": [
                                        {
                                          "id": 1,
                                          "tipoEndereco": "COMERCIAL",
                                          "rua": "Av. Central",
                                          "numero": "1000",
                                          "cidade": "São Paulo",
                                          "estado": "SP",
                                          "cep": "01001-000",
                                          "principal": true,
                                          "ativo": true
                                        }
                                      ],
                                      "ativo": true
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "type": "https://restauranthub.com/problems/domain-validation",
                                      "title": "Erro de validação de domínio",
                                      "status": 400,
                                      "detail": "Email já cadastrado",
                                      "instance": "/api/v1/users/1"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable("id") Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/users/{id}/password")
    @Operation(summary = "Alterar senha do usuário")
    public ResponseEntity<ApiMessageResponse> alterarSenha(
            @PathVariable("id") Long id,
            @Valid @RequestBody AlterarSenhaRequest request) {
        return ResponseEntity.ok(service.alterarSenha(id, request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Desativar usuário")
    @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso")
    public ResponseEntity<Void> desativarUsuario(@PathVariable("id") Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
