package com.restauranthub.restaurant_user_api.mappers;

import com.restauranthub.restaurant_user_api.domain.ItemCardapio;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioRequest;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioResponse;
import com.restauranthub.restaurant_user_api.entities.ItemCardapioEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class ItemCardapioMapper {

    public ItemCardapio toDomain(Long restauranteId, ItemCardapioRequest request) {
        if (request == null) {
            return null;
        }
        ItemCardapio item = ItemCardapio.create(
                restauranteId,
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getApenasNoLocal(),
                request.getCaminhoFoto());
        if (request.getAtivo() != null) {
            item.setAtivo(request.getAtivo());
        }
        return item;
    }

    public ItemCardapio toDomain(ItemCardapioEntity entity) {
        if (entity == null) {
            return null;
        }
        return ItemCardapio.builder()
                .id(entity.getId())
                .restauranteId(entity.getRestaurante() == null ? null : entity.getRestaurante().getId())
                .restauranteNome(entity.getRestaurante() == null ? null : entity.getRestaurante().getNome())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .apenasNoLocal(entity.getApenasNoLocal())
                .caminhoFoto(entity.getCaminhoFoto())
                .ativo(entity.getAtivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDomain(ItemCardapio domain, ItemCardapioEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setPreco(domain.getPreco());
        entity.setApenasNoLocal(domain.getApenasNoLocal());
        entity.setCaminhoFoto(domain.getCaminhoFoto());
        entity.setAtivo(domain.getAtivo());
    }

    public ItemCardapioResponse toResponse(ItemCardapio domain) {
        if (domain == null) {
            return null;
        }
        return ItemCardapioResponse.builder()
                .id(domain.getId())
                .restauranteId(domain.getRestauranteId())
                .restauranteNome(domain.getRestauranteNome())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .preco(domain.getPreco())
                .apenasNoLocal(domain.getApenasNoLocal())
                .caminhoFoto(domain.getCaminhoFoto())
                .ativo(domain.getAtivo())
                .dataCriacao(domain.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getCreatedAt(), ZoneOffset.UTC))
                .dataAtualizacao(domain.getUpdatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}
