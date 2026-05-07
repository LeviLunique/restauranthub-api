package com.restauranthub.restaurant_user_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapioRequest {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @NotBlank(message = "descrição é obrigatória")
    private String descricao;

    @NotNull(message = "preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "preço deve ser maior ou igual a zero")
    private BigDecimal preco;

    @NotNull(message = "apenasNoLocal é obrigatório")
    private Boolean apenasNoLocal;

    private String caminhoFoto;

    private Boolean ativo;
}
