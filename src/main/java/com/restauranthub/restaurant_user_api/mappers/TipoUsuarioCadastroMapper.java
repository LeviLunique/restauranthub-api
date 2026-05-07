package com.restauranthub.restaurant_user_api.mappers;

import com.restauranthub.restaurant_user_api.domain.TipoUsuarioCadastro;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroRequest;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroResponse;
import com.restauranthub.restaurant_user_api.entities.TipoUsuarioCadastroEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class TipoUsuarioCadastroMapper {

    public TipoUsuarioCadastro toDomain(TipoUsuarioCadastroRequest request) {
        if (request == null) {
            return null;
        }
        return TipoUsuarioCadastro.create(request.getNome(), request.getDescricao());
    }

    public TipoUsuarioCadastro toDomain(TipoUsuarioCadastroEntity entity) {
        if (entity == null) {
            return null;
        }
        return TipoUsuarioCadastro.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .ativo(entity.getAtivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public TipoUsuarioCadastroEntity toEntity(TipoUsuarioCadastro domain) {
        if (domain == null) {
            return null;
        }
        TipoUsuarioCadastroEntity entity = new TipoUsuarioCadastroEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setAtivo(domain.getAtivo());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public void updateEntityFromDomain(TipoUsuarioCadastro domain, TipoUsuarioCadastroEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setAtivo(domain.getAtivo());
    }

    public TipoUsuarioCadastroResponse toResponse(TipoUsuarioCadastro domain) {
        if (domain == null) {
            return null;
        }
        return TipoUsuarioCadastroResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .ativo(domain.getAtivo())
                .dataCriacao(domain.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getCreatedAt(), ZoneOffset.UTC))
                .dataAtualizacao(domain.getUpdatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}
