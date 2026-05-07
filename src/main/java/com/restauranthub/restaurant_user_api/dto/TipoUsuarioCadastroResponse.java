package com.restauranthub.restaurant_user_api.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioCadastroResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataAtualizacao;
}
