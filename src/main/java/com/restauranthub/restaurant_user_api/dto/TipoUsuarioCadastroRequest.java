package com.restauranthub.restaurant_user_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioCadastroRequest {

    @NotBlank(message = "nome do tipo é obrigatório")
    @Size(max = 50, message = "nome do tipo deve ter no máximo 50 caracteres")
    private String nome;

    @Size(max = 255, message = "descrição deve ter no máximo 255 caracteres")
    private String descricao;

    private Boolean ativo;
}
