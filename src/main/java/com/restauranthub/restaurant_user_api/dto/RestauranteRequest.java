package com.restauranthub.restaurant_user_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteRequest {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
    private String nome;

    @NotBlank(message = "tipoCozinha é obrigatório")
    @Size(max = 80, message = "tipoCozinha deve ter no máximo 80 caracteres")
    private String tipoCozinha;

    @NotBlank(message = "horarioFuncionamento é obrigatório")
    @Size(max = 120, message = "horarioFuncionamento deve ter no máximo 120 caracteres")
    private String horarioFuncionamento;

    @NotNull(message = "donoUsuarioId é obrigatório")
    private Long donoUsuarioId;

    @NotBlank(message = "rua é obrigatória")
    private String rua;

    private String numero;
    private String complemento;
    private String bairro;

    @NotBlank(message = "cidade é obrigatória")
    private String cidade;

    private String estado;

    @NotBlank(message = "cep é obrigatório")
    private String cep;

    private Boolean ativo;
}
