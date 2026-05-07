package com.restauranthub.restaurant_user_api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapioResponse {
    private Long id;
    private Long restauranteId;
    private String restauranteNome;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Boolean apenasNoLocal;
    private String caminhoFoto;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataAtualizacao;
}
