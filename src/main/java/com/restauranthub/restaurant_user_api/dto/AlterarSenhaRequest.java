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
public class AlterarSenhaRequest {

    @NotBlank(message = "senhaAtual é obrigatória")
    private String senhaAtual;

    @NotBlank(message = "novaSenha é obrigatória")
    @Size(min = 8, max = 72, message = "novaSenha deve ter entre 8 e 72 caracteres")
    private String novaSenha;
}
