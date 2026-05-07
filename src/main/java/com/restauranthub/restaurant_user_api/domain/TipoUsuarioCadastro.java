package com.restauranthub.restaurant_user_api.domain;

import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioCadastro {

    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Instant createdAt;
    private Instant updatedAt;

    public static TipoUsuarioCadastro create(String nome, String descricao) {
        TipoUsuarioCadastro tipo = new TipoUsuarioCadastro();
        tipo.setNome(nome);
        tipo.setDescricao(descricao);
        tipo.setAtivo(Boolean.TRUE);
        tipo.validateState();
        return tipo;
    }

    public void applyUpdate(String nome, String descricao, Boolean ativo) {
        if (nome != null) {
            this.nome = nome;
        }
        if (descricao != null) {
            this.descricao = descricao;
        }
        if (ativo != null) {
            this.ativo = ativo;
        }
        validateState();
    }

    public void validateState() {
        if (nome == null || nome.isBlank()) {
            throw new DomainValidationException("nome do tipo é obrigatório");
        }
        if (nome.length() > 50) {
            throw new DomainValidationException("nome do tipo deve ter no máximo 50 caracteres");
        }
        if (ativo == null) {
            ativo = Boolean.TRUE;
        }
    }
}
