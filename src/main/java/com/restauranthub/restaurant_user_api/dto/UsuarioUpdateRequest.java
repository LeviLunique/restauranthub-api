package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {

    @Size(min = 1, message = "nome não pode ser vazio")
    private String nome;

    @Email(message = "email inválido")
    private String email;

    private String telefone;

    private TipoUsuario tipoUsuario;

    @Nullable
    @Valid
    private List<EnderecoRequest> enderecos;

    private Boolean ativo;
}
