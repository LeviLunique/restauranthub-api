package com.restauranthub.restaurant_user_api.services;

import com.restauranthub.restaurant_user_api.dto.AlterarSenhaRequest;
import com.restauranthub.restaurant_user_api.dto.ApiMessageResponse;
import com.restauranthub.restaurant_user_api.dto.PageResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioRequest;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResumoResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioUpdateRequest;

public interface UsuarioService {
    UsuarioResponse create(UsuarioRequest dto);
    UsuarioResponse update(Long usuarioId, UsuarioUpdateRequest dto);
    UsuarioResponse findById(Long id);
    UsuarioResumoResponse findByEmail(String email);
    PageResponse<UsuarioResumoResponse> findAll(int page, int size, String sort);
    PageResponse<UsuarioResumoResponse> searchByNome(String nome, int page, int size, String sort);
    ApiMessageResponse alterarSenha(Long usuarioId, AlterarSenhaRequest dto);
    void desativar(Long usuarioId);
}
