package com.restauranthub.restaurant_user_api.services.impl;

import com.restauranthub.restaurant_user_api.config.security.JwtService;
import com.restauranthub.restaurant_user_api.dto.LoginRequest;
import com.restauranthub.restaurant_user_api.dto.LoginResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import com.restauranthub.restaurant_user_api.exceptions.ResourceNotFoundException;
import com.restauranthub.restaurant_user_api.mappers.UsuarioMapper;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import com.restauranthub.restaurant_user_api.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UsuarioRepository repository, UsuarioMapper mapper,
                           JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request == null) throw new DomainValidationException("request não pode ser nulo");
        UsuarioEntity entity = repository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + request.getEmail()));
        if (Boolean.FALSE.equals(entity.getAtivo())) {
            throw new DomainValidationException("Usuário inativo");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );
        } catch (AuthenticationException e) {
            throw new DomainValidationException("Credenciais inválidas");
        }
        if (!authentication.isAuthenticated()) {
            throw new DomainValidationException("Credenciais inválidas");
        }

        UsuarioResumoResponse resumo = mapper.toResumo(mapper.toDomain(entity));
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(resumo)
                .build();
    }
}
