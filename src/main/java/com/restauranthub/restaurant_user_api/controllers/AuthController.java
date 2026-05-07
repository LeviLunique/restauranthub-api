package com.restauranthub.restaurant_user_api.controllers;

import com.restauranthub.restaurant_user_api.dto.LoginRequest;
import com.restauranthub.restaurant_user_api.dto.LoginResponse;
import com.restauranthub.restaurant_user_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação e emissão de JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Valida email e senha e retorna um token JWT para acesso aos endpoints protegidos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(
                            name = "Sucesso",
                            value = """
                                    {
                                      "token": "eyJhbGciOiJIUzI1NiJ9...",
                                      "tipo": "Bearer",
                                      "usuario": {
                                        "id": 1,
                                        "nome": "Administrador",
                                        "email": "admin@restauranthub.com",
                                        "tipoUsuario": "ADMIN",
                                        "ativo": true
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Payload inválido",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Erro de validação",
                            value = """
                                    {
                                      "type": "https://restauranthub.com/problems/validation-error",
                                      "title": "Erro de validação",
                                      "status": 400,
                                      "detail": "Dados inválidos",
                                      "instance": "/api/v1/auth/login",
                                      "errors": {
                                        "email": "email é obrigatório",
                                        "senha": "senha é obrigatória"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = org.springframework.http.ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Não autorizado",
                            value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Unauthorized",
                                      "status": 401,
                                      "detail": "Bad credentials",
                                      "instance": "/api/v1/auth/login"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
