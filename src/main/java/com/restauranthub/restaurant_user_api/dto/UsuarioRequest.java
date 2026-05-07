package com.restauranthub.restaurant_user_api.dto;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class UsuarioRequest {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Email(message = "email inválido")
    @NotBlank(message = "email é obrigatório")
    private String email;

    @NotBlank(message = "login é obrigatório")
    private String login;

    @NotBlank(message = "senha é obrigatória")
    @Size(min = 8, max = 72, message = "senha deve ter entre 8 e 72 caracteres")
    private String senha;

    @NotNull(message = "tipoUsuario é obrigatório")
    private TipoUsuario tipoUsuario;

    private String telefone;

    @NotEmpty(message = "ao menos um endereço deve ser informado")
    @Valid
    private List<EnderecoRequest> enderecos;
}
