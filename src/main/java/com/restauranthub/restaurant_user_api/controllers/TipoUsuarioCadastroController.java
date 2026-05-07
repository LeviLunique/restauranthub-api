package com.restauranthub.restaurant_user_api.controllers;

import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroRequest;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.services.TipoUsuarioCadastroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/user-types")
@Tag(name = "Tipos de Usuário", description = "Catálogo de tipos de usuário e associação com usuários existentes")
public class TipoUsuarioCadastroController {

    private final TipoUsuarioCadastroService service;

    public TipoUsuarioCadastroController(TipoUsuarioCadastroService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar tipo de usuário")
    public ResponseEntity<TipoUsuarioCadastroResponse> create(@Valid @RequestBody TipoUsuarioCadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tipo de usuário")
    public ResponseEntity<TipoUsuarioCadastroResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody TipoUsuarioCadastroRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de usuário por id")
    public ResponseEntity<TipoUsuarioCadastroResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar tipos de usuário")
    public ResponseEntity<List<TipoUsuarioCadastroResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{typeId}/users/{userId}")
    @Operation(summary = "Associar tipo de usuário a um usuário existente")
    public ResponseEntity<UsuarioResponse> associateToUser(
            @PathVariable("typeId") Long typeId,
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.associateToUser(typeId, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tipo de usuário")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
