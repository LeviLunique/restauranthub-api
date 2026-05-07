package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String login;
    private Long tipoUsuarioId;
    private String tipoUsuarioNome;
    private TipoUsuario tipoUsuario;
    private String telefone;
    private List<EnderecoResponse> enderecos;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataAtualizacao;
}
