package com.restauranthub.restaurant_user_api.controllers;

import com.restauranthub.restaurant_user_api.dto.ItemCardapioRequest;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioResponse;
import com.restauranthub.restaurant_user_api.services.ItemCardapioService;
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
@RequestMapping("/api/v1")
@Tag(name = "Cardápio", description = "CRUD de itens de cardápio")
public class ItemCardapioController {

    private final ItemCardapioService service;

    public ItemCardapioController(ItemCardapioService service) {
        this.service = service;
    }

    @PostMapping("/restaurants/{restaurantId}/menu-items")
    @Operation(summary = "Criar item de cardápio para um restaurante")
    public ResponseEntity<ItemCardapioResponse> create(
            @PathVariable("restaurantId") Long restaurantId,
            @Valid @RequestBody ItemCardapioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(restaurantId, request));
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    @Operation(summary = "Listar itens de cardápio por restaurante")
    public ResponseEntity<List<ItemCardapioResponse>> findByRestaurante(@PathVariable("restaurantId") Long restaurantId) {
        return ResponseEntity.ok(service.findByRestaurante(restaurantId));
    }

    @GetMapping("/menu-items/{id}")
    @Operation(summary = "Buscar item de cardápio por id")
    public ResponseEntity<ItemCardapioResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/menu-items/{id}")
    @Operation(summary = "Atualizar item de cardápio")
    public ResponseEntity<ItemCardapioResponse> update(@PathVariable("id") Long id, @Valid @RequestBody ItemCardapioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/menu-items/{id}")
    @Operation(summary = "Excluir item de cardápio")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
