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
public class RestauranteResponse {
    private Long id;
    private String nome;
    private String tipoCozinha;
    private String horarioFuncionamento;
    private Long donoUsuarioId;
    private String donoNome;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataAtualizacao;
}
