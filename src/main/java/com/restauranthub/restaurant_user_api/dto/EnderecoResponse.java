package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponse {
    private Long id;
    private TipoEndereco tipoEndereco;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean principal;
    private Boolean ativo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
