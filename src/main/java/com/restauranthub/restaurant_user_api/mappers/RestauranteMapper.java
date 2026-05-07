package com.restauranthub.restaurant_user_api.mappers;

import com.restauranthub.restaurant_user_api.domain.Restaurante;
import com.restauranthub.restaurant_user_api.dto.RestauranteRequest;
import com.restauranthub.restaurant_user_api.dto.RestauranteResponse;
import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class RestauranteMapper {

    public Restaurante toDomain(RestauranteRequest request) {
        if (request == null) {
            return null;
        }
        Restaurante restaurante = Restaurante.create(
                request.getNome(),
                request.getTipoCozinha(),
                request.getHorarioFuncionamento(),
                request.getDonoUsuarioId(),
                request.getRua(),
                request.getNumero(),
                request.getComplemento(),
                request.getBairro(),
                request.getCidade(),
                request.getEstado(),
                request.getCep());
        if (request.getAtivo() != null) {
            restaurante.setAtivo(request.getAtivo());
        }
        return restaurante;
    }

    public Restaurante toDomain(RestauranteEntity entity) {
        if (entity == null) {
            return null;
        }
        return Restaurante.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .tipoCozinha(entity.getTipoCozinha())
                .horarioFuncionamento(entity.getHorarioFuncionamento())
                .donoUsuarioId(entity.getDono() == null ? null : entity.getDono().getId())
                .donoNome(entity.getDono() == null ? null : entity.getDono().getNome())
                .rua(entity.getRua())
                .numero(entity.getNumero())
                .complemento(entity.getComplemento())
                .bairro(entity.getBairro())
                .cidade(entity.getCidade())
                .estado(entity.getEstado())
                .cep(entity.getCep())
                .ativo(entity.getAtivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDomain(Restaurante domain, RestauranteEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setNome(domain.getNome());
        entity.setTipoCozinha(domain.getTipoCozinha());
        entity.setHorarioFuncionamento(domain.getHorarioFuncionamento());
        entity.setRua(domain.getRua());
        entity.setNumero(domain.getNumero());
        entity.setComplemento(domain.getComplemento());
        entity.setBairro(domain.getBairro());
        entity.setCidade(domain.getCidade());
        entity.setEstado(domain.getEstado());
        entity.setCep(domain.getCep());
        entity.setAtivo(domain.getAtivo());
    }

    public RestauranteResponse toResponse(Restaurante domain) {
        if (domain == null) {
            return null;
        }
        return RestauranteResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .tipoCozinha(domain.getTipoCozinha())
                .horarioFuncionamento(domain.getHorarioFuncionamento())
                .donoUsuarioId(domain.getDonoUsuarioId())
                .donoNome(domain.getDonoNome())
                .rua(domain.getRua())
                .numero(domain.getNumero())
                .complemento(domain.getComplemento())
                .bairro(domain.getBairro())
                .cidade(domain.getCidade())
                .estado(domain.getEstado())
                .cep(domain.getCep())
                .ativo(domain.getAtivo())
                .dataCriacao(domain.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getCreatedAt(), ZoneOffset.UTC))
                .dataAtualizacao(domain.getUpdatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
}
