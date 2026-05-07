package com.restauranthub.restaurant_user_api.mappers;

import com.restauranthub.restaurant_user_api.domain.Endereco;
import com.restauranthub.restaurant_user_api.dto.EnderecoRequest;
import com.restauranthub.restaurant_user_api.dto.EnderecoResponse;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toDomain(EnderecoRequest dto) {
        if (dto == null) return null;
        Endereco e = Endereco.create(
                dto.getTipoEndereco(),
                dto.getRua(),
                dto.getNumero(),
                dto.getComplemento(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                dto.getCep(),
                dto.getPrincipal(),
                dto.getAtivo()
        );
        e.setId(dto.getId());
        return e;
    }

    public EnderecoEntity toEntity(Endereco domain) {
        if (domain == null) return null;
        EnderecoEntity e = new EnderecoEntity();
        e.setId(domain.getId());
        e.setTipoEndereco(domain.getTipoEndereco());
        e.setRua(domain.getRua());
        e.setNumero(domain.getNumero());
        e.setComplemento(domain.getComplemento());
        e.setBairro(domain.getBairro());
        e.setCidade(domain.getCidade());
        e.setEstado(domain.getEstado());
        e.setCep(domain.getCep());
        e.setPrincipal(domain.getPrincipal());
        e.setAtivo(domain.getAtivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }

    public Endereco toDomain(EnderecoEntity entity) {
        if (entity == null) return null;
        Endereco d = new Endereco();
        d.setId(entity.getId());
        d.setTipoEndereco(entity.getTipoEndereco());
        d.setRua(entity.getRua());
        d.setNumero(entity.getNumero());
        d.setComplemento(entity.getComplemento());
        d.setBairro(entity.getBairro());
        d.setCidade(entity.getCidade());
        d.setEstado(entity.getEstado());
        d.setCep(entity.getCep());
        d.setPrincipal(entity.getPrincipal());
        d.setAtivo(entity.getAtivo());
        d.setCreatedAt(entity.getCreatedAt());
        d.setUpdatedAt(entity.getUpdatedAt());
        return d;
    }

    public void updateEntityFromDomain(Endereco domain, EnderecoEntity entity) {
        if (domain == null || entity == null) return;
        entity.setTipoEndereco(domain.getTipoEndereco());
        entity.setRua(domain.getRua());
        entity.setNumero(domain.getNumero());
        entity.setComplemento(domain.getComplemento());
        entity.setBairro(domain.getBairro());
        entity.setCidade(domain.getCidade());
        entity.setEstado(domain.getEstado());
        entity.setCep(domain.getCep());
        entity.setPrincipal(domain.getPrincipal());
        entity.setAtivo(domain.getAtivo());
    }

    public EnderecoResponse toResponse(Endereco domain) {
        if (domain == null) return null;
        EnderecoResponse r = new EnderecoResponse();
        r.setId(domain.getId());
        r.setTipoEndereco(domain.getTipoEndereco());
        r.setRua(domain.getRua());
        r.setNumero(domain.getNumero());
        r.setComplemento(domain.getComplemento());
        r.setBairro(domain.getBairro());
        r.setCidade(domain.getCidade());
        r.setEstado(domain.getEstado());
        r.setCep(domain.getCep());
        r.setPrincipal(domain.getPrincipal());
        r.setAtivo(domain.getAtivo());
        r.setCreatedAt(domain.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getCreatedAt(), ZoneOffset.UTC));
        r.setUpdatedAt(domain.getUpdatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getUpdatedAt(), ZoneOffset.UTC));
        return r;
    }
}
