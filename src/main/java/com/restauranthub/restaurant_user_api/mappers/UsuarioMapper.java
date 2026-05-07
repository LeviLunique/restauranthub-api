package com.restauranthub.restaurant_user_api.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.restauranthub.restaurant_user_api.domain.Endereco;
import com.restauranthub.restaurant_user_api.domain.Usuario;
import com.restauranthub.restaurant_user_api.dto.EnderecoRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;

@Component
public class UsuarioMapper {

    private final EnderecoMapper enderecoMapper;

    public UsuarioMapper(EnderecoMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    // DTO criação -> domínio
    public Usuario toDomain(UsuarioRequest dto) {
        if (dto == null)
            return null;
        List<Endereco> enderecos = mapEnderecos(dto.getEnderecos());
        return Usuario.create(
                dto.getNome(),
                dto.getEmail(),
                dto.getLogin(),
                dto.getSenha(),
                dto.getTipoUsuario(),
                dto.getTelefone(),
                enderecos);
    }

    // DTO atualização -> domínio (parcial)
    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null)
            return null;
        Usuario u = new Usuario();
        u.setId(entity.getId());
        u.setNome(entity.getNome());
        u.setEmail(entity.getEmail());
        u.setLogin(entity.getLogin());
        u.setSenha(entity.getSenha());
        u.setTipoUsuario(entity.getTipoUsuario());
        u.setTipoUsuarioId(entity.getTipoUsuarioCadastro() == null ? null : entity.getTipoUsuarioCadastro().getId());
        u.setTipoUsuarioNome(entity.getTipoUsuarioCadastro() == null ? entity.getTipoUsuario().name() : entity.getTipoUsuarioCadastro().getNome());
        u.setTelefone(entity.getTelefone());
        u.setAtivo(entity.getAtivo());
        u.setCreatedAt(entity.getCreatedAt());
        u.setUpdatedAt(entity.getUpdatedAt());
        u.setEnderecos(entity.getEnderecos().stream()
                .map(enderecoMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new)));
        return u;
    }

    // Domínio -> Entity
    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null)
            return null;
        UsuarioEntity e = new UsuarioEntity();
        e.setId(domain.getId());
        e.setNome(domain.getNome());
        e.setEmail(domain.getEmail());
        e.setLogin(domain.getLogin());
        e.setSenha(domain.getSenha());
        e.setTipoUsuario(domain.getTipoUsuario());
        e.setTelefone(domain.getTelefone());
        e.setAtivo(domain.getAtivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getEnderecos() != null) {
            List<EnderecoEntity> enderecos = domain.getEnderecos().stream()
                    .map(enderecoMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new));
            enderecos.forEach(en -> en.setUsuario(e));
            e.setEnderecos(enderecos);
        }
        return e;
    }

    // Atualiza entity gerenciada a partir do domínio (evita recriar)
    public void updateEntityFromDomain(Usuario domain, UsuarioEntity entity) {
        if (domain == null || entity == null)
            return;
        entity.setNome(domain.getNome());
        entity.setEmail(domain.getEmail());
        entity.setLogin(domain.getLogin());
        entity.setSenha(domain.getSenha());
        entity.setTipoUsuario(domain.getTipoUsuario());
        entity.setTelefone(domain.getTelefone());
        entity.setAtivo(domain.getAtivo());

        // Sincroniza endereços: atualiza existentes, cria novos, remove ausentes
        List<EnderecoEntity> managed = entity.getEnderecos() == null ? new ArrayList<>() : entity.getEnderecos();

        // Atualiza ou cria
        List<EnderecoEntity> atualizados = new ArrayList<>();
        for (Endereco d : domain.getEnderecos()) {
            EnderecoEntity existente = managed.stream()
                    .filter(e -> e.getId() != null && Objects.equals(e.getId(), d.getId()))
                    .findFirst()
                    .orElse(null);
            if (existente != null) {
                enderecoMapper.updateEntityFromDomain(d, existente);
                atualizados.add(existente);
            } else {
                EnderecoEntity novo = enderecoMapper.toEntity(d);
                novo.setUsuario(entity);
                atualizados.add(novo);
            }
        }
        // Remove órfãos
        managed.clear();
        managed.addAll(atualizados);
        entity.setEnderecos(managed);
    }

    // Domain -> Response DTO
    public UsuarioResponse toResponse(Usuario domain) {
        if (domain == null)
            return null;
        UsuarioResponse r = new UsuarioResponse();
        r.setId(domain.getId());
        r.setNome(domain.getNome());
        r.setEmail(domain.getEmail());
        r.setLogin(domain.getLogin());
        r.setTipoUsuarioId(domain.getTipoUsuarioId());
        r.setTipoUsuarioNome(domain.getTipoUsuarioNome());
        r.setTipoUsuario(domain.getTipoUsuario());
        r.setTelefone(domain.getTelefone());
        r.setAtivo(domain.getAtivo());
        r.setEnderecos(domain.getEnderecos() == null ? List.of()
                : domain.getEnderecos().stream().map(enderecoMapper::toResponse).toList());
        r.setDataCriacao(
                domain.getCreatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getCreatedAt(), ZoneOffset.UTC));
        r.setDataAtualizacao(
                domain.getUpdatedAt() == null ? null : OffsetDateTime.ofInstant(domain.getUpdatedAt(), ZoneOffset.UTC));
        return r;
    }

    public UsuarioResumoResponse toResumo(Usuario domain) {
        if (domain == null)
            return null;
        UsuarioResumoResponse r = new UsuarioResumoResponse();
        r.setId(domain.getId());
        r.setNome(domain.getNome());
        r.setEmail(domain.getEmail());
        r.setTipoUsuarioId(domain.getTipoUsuarioId());
        r.setTipoUsuarioNome(domain.getTipoUsuarioNome());
        r.setTipoUsuario(domain.getTipoUsuario());
        r.setAtivo(domain.getAtivo());
        return r;
    }

    public void updateDomainFromDto(UsuarioUpdateRequest dto, Usuario domain) {
        if (dto == null || domain == null)
            return;
        List<Endereco> enderecos = dto.getEnderecos() == null ? null : mapEnderecos(dto.getEnderecos());
        domain.applyUpdate(
                dto.getNome(),
                dto.getEmail(),
                null,
                dto.getTelefone(),
                dto.getTipoUsuario(),
                enderecos);
        if (dto.getAtivo() != null) {
            if (Boolean.TRUE.equals(dto.getAtivo())) {
                domain.reativar();
            } else {
                domain.desativar();
            }
        }
    }

    private List<Endereco> mapEnderecos(List<EnderecoRequest> enderecos) {
        if (enderecos == null)
            return List.of();
        return enderecos.stream()
                .map(enderecoMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
