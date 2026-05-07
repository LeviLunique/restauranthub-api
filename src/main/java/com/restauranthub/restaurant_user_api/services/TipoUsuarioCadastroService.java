package com.restauranthub.restaurant_user_api.services;

import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroRequest;
import com.restauranthub.restaurant_user_api.dto.TipoUsuarioCadastroResponse;
import com.restauranthub.restaurant_user_api.dto.UsuarioResponse;
import java.util.List;

public interface TipoUsuarioCadastroService {

    TipoUsuarioCadastroResponse create(TipoUsuarioCadastroRequest request);

    TipoUsuarioCadastroResponse update(Long id, TipoUsuarioCadastroRequest request);

    TipoUsuarioCadastroResponse findById(Long id);

    List<TipoUsuarioCadastroResponse> findAll();

    void delete(Long id);

    UsuarioResponse associateToUser(Long tipoUsuarioId, Long usuarioId);
}
