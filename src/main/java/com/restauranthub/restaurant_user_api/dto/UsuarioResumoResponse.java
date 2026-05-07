package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResumoResponse {
    private Long id;
    private String nome;
    private String email;
    private Long tipoUsuarioId;
    private String tipoUsuarioNome;
    private TipoUsuario tipoUsuario;
    private Boolean ativo;
}
