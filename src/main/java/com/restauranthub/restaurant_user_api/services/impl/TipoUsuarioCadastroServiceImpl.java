package com.restauranthub.restaurant_user_api.services.impl;

import com.restauranthub.restaurant_user_api.domain.TipoUsuarioCadastro;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroRequest;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.entities.TipoUsuarioCadastroEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.TipoUsuarioCadastroMapper;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.repositories.TipoUsuarioCadastroRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.TipoUsuarioCadastroService;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoUsuarioCadastroServiceImpl implements TipoUsuarioCadastroService {

    private final TipoUsuarioCadastroRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioCadastroMapper mapper;
    private final UsuarioMapper usuarioMapper;

    public TipoUsuarioCadastroServiceImpl(
            TipoUsuarioCadastroRepository repository,
            UsuarioRepository usuarioRepository,
            TipoUsuarioCadastroMapper mapper,
            UsuarioMapper usuarioMapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public TipoUsuarioCadastroResponse create(TipoUsuarioCadastroRequest request) {
        TipoUsuarioCadastro domain = mapper.toDomain(request);
        domain.setNome(normalizarNome(domain.getNome()));
        validarNomeEnum(domain.getNome());
        validarUnicidade(domain.getNome(), null);
        TipoUsuarioCadastroEntity saved = repository.saveAndFlush(mapper.toEntity(domain));
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional
    public TipoUsuarioCadastroResponse update(Long id, TipoUsuarioCadastroRequest request) {
        TipoUsuarioCadastroEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));
        TipoUsuarioCadastro domain = mapper.toDomain(existing);
        String nome = request.getNome() == null ? null : normalizarNome(request.getNome());
        if (nome != null) {
            validarNomeEnum(nome);
            validarUnicidade(nome, id);
        }
        domain.applyUpdate(nome, request.getDescricao(), request.getAtivo());
        mapper.updateEntityFromDomain(domain, existing);
        TipoUsuarioCadastroEntity saved = repository.saveAndFlush(existing);
        sincronizarUsuariosPorNome(saved);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public TipoUsuarioCadastroResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoUsuarioCadastroResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TipoUsuarioCadastroEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + id));
        long vinculados = usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getTipoUsuarioCadastro() != null && existing.getId().equals(usuario.getTipoUsuarioCadastro().getId()))
                .count();
        if (vinculados > 0) {
            throw new DomainValidationException("Tipo de usuário está associado a usuários existentes");
        }
        repository.delete(existing);
    }

    @Override
    @Transactional
    public UsuarioResponse associateToUser(Long tipoUsuarioId, Long usuarioId) {
        TipoUsuarioCadastroEntity tipo = repository.findById(tipoUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de usuário não encontrado com id: " + tipoUsuarioId));
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId));
        usuario.setTipoUsuarioCadastro(tipo);
        usuario.setTipoUsuario(parseTipoUsuario(tipo.getNome()));
        UsuarioEntity saved = usuarioRepository.saveAndFlush(usuario);
        return usuarioMapper.toResponse(usuarioMapper.toDomain(saved));
    }

    private void validarUnicidade(String nome, Long ignoreId) {
        repository.findByNomeIgnoreCase(nome).ifPresent(entity -> {
            if (!entity.getId().equals(ignoreId)) {
                throw new DomainValidationException("Tipo de usuário já cadastrado");
            }
        });
    }

    private void validarNomeEnum(String nome) {
        parseTipoUsuario(nome);
    }

    private TipoUsuario parseTipoUsuario(String nome) {
        try {
            return TipoUsuario.valueOf(nome.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("nome do tipo deve corresponder a um tipo suportado pelo sistema");
        }
    }

    private String normalizarNome(String nome) {
        if (nome == null) {
            return null;
        }
        return nome.trim().toUpperCase(Locale.ROOT);
    }

    private void sincronizarUsuariosPorNome(TipoUsuarioCadastroEntity tipo) {
        TipoUsuario enumValue = parseTipoUsuario(tipo.getNome());
        usuarioRepository.findByTipoUsuario(enumValue, org.springframework.data.domain.Pageable.unpaged())
                .forEach(usuario -> usuario.setTipoUsuarioCadastro(tipo));
    }
}
