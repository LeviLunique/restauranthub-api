package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoRequest {

    private Long id;

    @NotNull(message = "tipoEndereco é obrigatório")
    private TipoEndereco tipoEndereco;

    @NotBlank(message = "rua é obrigatória")
    private String rua;

    @NotBlank(message = "numero é obrigatório")
    private String numero;

    private String complemento;

    private String bairro;

    @NotBlank(message = "cidade é obrigatória")
    private String cidade;

    @Size(max = 2, message = "estado deve conter a sigla de 2 caracteres")
    private String estado;

    @NotBlank(message = "cep é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "cep deve seguir o formato 00000-000")
    private String cep;

    @NotNull(message = "principal deve ser informado")
    private Boolean principal;

    private Boolean ativo;
}
