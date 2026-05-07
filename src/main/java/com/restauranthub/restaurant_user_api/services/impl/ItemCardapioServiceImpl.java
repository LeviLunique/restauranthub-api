package com.restauranthub.restaurant_user_api.services.impl;

import com.restauranthub.restaurant_user_api.domain.ItemCardapio;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioRequest;
import com.restauranthub.restaurant_user_api.dto.ItemCardapioResponse;
import com.restauranthub.restaurant_user_api.entities.ItemCardapioEntity;
import com.restauranthub.restaurant_user_api.entities.RestauranteEntity;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.ItemCardapioMapper;
import com.restauranthub.restaurant_user_api.repositories.ItemCardapioRepository;
import com.restauranthub.restaurant_user_api.repositories.RestauranteRepository;
import com.restauranthub.restaurant_user_api.services.ItemCardapioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemCardapioServiceImpl implements ItemCardapioService {

    private final ItemCardapioRepository repository;
    private final RestauranteRepository restauranteRepository;
    private final ItemCardapioMapper mapper;

    public ItemCardapioServiceImpl(
            ItemCardapioRepository repository,
            RestauranteRepository restauranteRepository,
            ItemCardapioMapper mapper) {
        this.repository = repository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ItemCardapioResponse create(Long restauranteId, ItemCardapioRequest request) {
        RestauranteEntity restaurante = buscarRestaurante(restauranteId);
        ItemCardapio domain = mapper.toDomain(restauranteId, request);
        ItemCardapioEntity entity = new ItemCardapioEntity();
        mapper.updateEntityFromDomain(domain, entity);
        entity.setRestaurante(restaurante);
        ItemCardapioEntity saved = repository.saveAndFlush(entity);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional
    public ItemCardapioResponse update(Long id, ItemCardapioRequest request) {
        ItemCardapioEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de cardápio não encontrado com id: " + id));
        ItemCardapio domain = mapper.toDomain(existing);
        domain.applyUpdate(
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getApenasNoLocal(),
                request.getCaminhoFoto(),
                request.getAtivo());
        mapper.updateEntityFromDomain(domain, existing);
        ItemCardapioEntity saved = repository.saveAndFlush(existing);
        return mapper.toResponse(mapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCardapioResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item de cardápio não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCardapioResponse> findByRestaurante(Long restauranteId) {
        buscarRestaurante(restauranteId);
        return repository.findByRestauranteId(restauranteId).stream()
                .map(mapper::toDomain)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ItemCardapioEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de cardápio não encontrado com id: " + id));
        repository.delete(existing);
    }

    private RestauranteEntity buscarRestaurante(Long restauranteId) {
        return restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado com id: " + restauranteId));
    }
}
