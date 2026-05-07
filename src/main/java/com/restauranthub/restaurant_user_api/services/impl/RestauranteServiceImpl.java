package com.restauranthub.restaurant_user_api.services.impl;

import com.restauranthub.restaurant_user_api.domain.Restaurante;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.dto.RestauranteRequest;
import com.restauranthub.restaurant_user_api.dto.RestauranteResponse;
import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.RestauranteMapper;
import com.restauranthub.restaurant_user_api.repositories.RestauranteRepository;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.RestauranteService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteMapper mapper;

    public RestauranteServiceImpl(RestauranteRepository repository, UsuarioRepository usuarioRepository, RestauranteMapper mapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestauranteResponse create(RestauranteRequest request) {
        Restaurante domain = mapper.toDomain(request);
        UsuarioEntity dono = buscarDonoValido(domain.getDonoUsuarioId());
        RestauranteEntity entity = new RestauranteEntity();
        mapper.updateEntityFromDomain(domain, entity);
        entity.setDono(dono);
        RestauranteEntity saved = repository.saveAndFlush(entity);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional
    public RestauranteResponse update(Long id, RestauranteRequest request) {
        RestauranteEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
        Restaurante domain = mapper.toDomain(existing);
        domain.applyUpdate(
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
                request.getCep(),
                request.getAtivo());
        mapper.updateEntityFromDomain(domain, existing);
        if (domain.getDonoUsuarioId() != null) {
            existing.setDono(buscarDonoValido(domain.getDonoUsuarioId()));
        }
        RestauranteEntity saved = repository.saveAndFlush(existing);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public RestauranteResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestauranteResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RestauranteEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + id));
        repository.delete(existing);
    }

    private UsuarioEntity buscarDonoValido(Long donoUsuarioId) {
        UsuarioEntity dono = usuarioRepository.findById(donoUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário dono não encontrado com id: " + donoUsuarioId));
        if (!TipoUsuario.DONO_RESTAURANTE.equals(dono.getTipoUsuario())) {
            throw new DomainValidationException("Usuário informado não é um dono de restaurante");
        }
        if (Boolean.FALSE.equals(dono.getAtivo())) {
            throw new DomainValidationException("Usuário dono está inativo");
        }
        return dono;
    }
}
