package com.restauranthub.restaurant_user_api.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restauranthub.restaurant_user_api.domain.Usuario;
import com.restauranthub.restaurant_user_api.dto.AlterarSenhaRequest;
import com.restauranthub.restaurant_user_api.dto.ApiMessageResponse;
import com.restauranthub.restaurant_user_api.dto.PageMetadata;
import com.restauranthub.restaurant_user_api.dto.PageResponse;
import com.restauranthub.restaurant_user_api.dto.SortResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.repositories.TipoUsuarioCadastroRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository repository;
    private final TipoUsuarioCadastroRepository tipoUsuarioCadastroRepository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository repository, TipoUsuarioCadastroRepository tipoUsuarioCadastroRepository, UsuarioMapper mapper) {
        this.repository = repository;
        this.tipoUsuarioCadastroRepository = tipoUsuarioCadastroRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest dto) {
        if (dto == null)
            throw new DomainValidationException("dto não pode ser nulo");
        normalizarCamposCriacao(dto);
        validarUnicidade(dto.getEmail(), dto.getLogin(), null);

        Usuario domain = mapper.toDomain(dto);
        domain.atualizarSenha(dto.getSenha());
        domain.setSenha(passwordEncoder.encode(domain.getSenha()));

        UsuarioEntity entity = mapper.toEntity(domain);
        sincronizarTipoUsuario(entity);
        UsuarioEntity saved = repository.saveAndFlush(entity);
        log.info("Usuário criado id={}", saved.getId());
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional
    public UsuarioResponse update(Long usuarioId, UsuarioUpdateRequest dto) {
        if (usuarioId == null)
            throw new DomainValidationException("usuarioId não pode ser nulo");
        if (dto == null)
            throw new DomainValidationException("dto não pode ser nulo");
        normalizarCamposAtualizacao(dto);

        UsuarioEntity existing = repository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId));

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(normalizarTexto(existing.getEmail()))) {
            validarUnicidade(dto.getEmail(), null, usuarioId);
        }

        Usuario domain = mapper.toDomain(existing);
        mapper.updateDomainFromDto(dto, domain);

        mapper.updateEntityFromDomain(domain, existing);
        sincronizarTipoUsuario(existing);
        UsuarioEntity saved = repository.saveAndFlush(existing);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        UsuarioEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        return mapper.toResponse(mapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResumoResponse findByEmail(String email) {
        UsuarioEntity entity = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        return mapper.toResumo(mapper.toDomain(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResumoResponse> findAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, resolverSort(sort));
        Page<UsuarioEntity> p = repository.findAll(pageable);
        return toPageResponse(p.map(e -> mapper.toResumo(mapper.toDomain(e))));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResumoResponse> searchByNome(String nome, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, resolverSort(sort));
        Page<UsuarioEntity> p = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return toPageResponse(p.map(e -> mapper.toResumo(mapper.toDomain(e))));
    }

    @Override
    @Transactional
    public ApiMessageResponse alterarSenha(Long usuarioId, AlterarSenhaRequest dto) {
        if (usuarioId == null)
            throw new DomainValidationException("usuarioId não pode ser nulo");
        if (dto == null)
            throw new DomainValidationException("dto não pode ser nulo");

        UsuarioEntity entity = repository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), entity.getSenha())) {
            throw new DomainValidationException("Senha atual inválida");
        }

        Usuario domain = mapper.toDomain(entity);
        domain.atualizarSenha(dto.getNovaSenha());
        domain.setSenha(passwordEncoder.encode(domain.getSenha()));

        mapper.updateEntityFromDomain(domain, entity);
        repository.saveAndFlush(entity);

        return ApiMessageResponse.builder().message("Senha alterada com sucesso").build();
    }

    @Override
    @Transactional
    public void desativar(Long usuarioId) {
        if (usuarioId == null)
            throw new DomainValidationException("usuarioId não pode ser nulo");
        UsuarioEntity entity = repository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + usuarioId));
        Usuario domain = mapper.toDomain(entity);
        domain.desativar();
        mapper.updateEntityFromDomain(domain, entity);
        repository.saveAndFlush(entity);
    }

    private void validarUnicidade(String email, String login, Long ignoreId) {
        if (email != null) {
            repository.findByEmailIgnoreCase(email).ifPresent(u -> {
                if (!u.getId().equals(ignoreId)) {
                    throw new DomainValidationException("Email já cadastrado");
                }
            });
        }
        if (login != null) {
            repository.findByLoginIgnoreCase(login).ifPresent(u -> {
                if (!u.getId().equals(ignoreId)) {
                    throw new DomainValidationException("Login já cadastrado");
                }
            });
        }
    }

    private void normalizarCamposCriacao(UsuarioRequest dto) {
        dto.setNome(normalizarTexto(dto.getNome()));
        dto.setEmail(normalizarEmail(dto.getEmail()));
        dto.setLogin(normalizarTexto(dto.getLogin()));
        dto.setTelefone(normalizarTexto(dto.getTelefone()));
    }

    private void normalizarCamposAtualizacao(UsuarioUpdateRequest dto) {
        dto.setNome(normalizarTexto(dto.getNome()));
        dto.setEmail(normalizarEmail(dto.getEmail()));
        dto.setTelefone(normalizarTexto(dto.getTelefone()));
    }

    private String normalizarEmail(String value) {
        String normalized = normalizarTexto(value);
        return normalized == null ? null : normalized.toLowerCase();
    }

    private String normalizarTexto(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void sincronizarTipoUsuario(UsuarioEntity entity) {
        if (entity.getTipoUsuario() == null) {
            return;
        }
        tipoUsuarioCadastroRepository.findByNomeIgnoreCase(entity.getTipoUsuario().name())
                .ifPresent(entity::setTipoUsuarioCadastro);
    }

    private Sort resolverSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "nome");
        }
        String[] parts = sort.split(",");
        String prop = parts[0];
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }

    private PageResponse<UsuarioResumoResponse> toPageResponse(Page<UsuarioResumoResponse> page) {
        Sort sort = page.getPageable().getSort();
        SortResponse sortResponse = SortResponse.builder()
                .sorted(sort.isSorted())
                .unsorted(sort.isUnsorted())
                .empty(sort.isEmpty())
                .build();
        PageMetadata meta = PageMetadata.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .sort(sortResponse)
                .build();
        return new PageResponse<>(page.getContent(), meta, page.getTotalElements(), page.getTotalPages());
    }
}
