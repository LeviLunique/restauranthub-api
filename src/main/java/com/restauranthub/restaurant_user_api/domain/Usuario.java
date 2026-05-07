package com.restauranthub.restaurant_user_api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private TipoUsuario tipoUsuario;
    private Long tipoUsuarioId;
    private String tipoUsuarioNome;
    private String telefone;
    private List<Endereco> enderecos = new ArrayList<>();
    private Boolean ativo;
    private Instant createdAt;
    private Instant updatedAt;

    public static Usuario create(
            String nome,
            String email,
            String login,
            String senha,
            TipoUsuario tipoUsuario,
            String telefone,
            List<Endereco> enderecos) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setLogin(login);
        u.setSenha(senha);
        u.setTipoUsuario(tipoUsuario);
        u.setTelefone(telefone);
        u.setAtivo(Boolean.TRUE);
        u.replaceEnderecos(enderecos);
        u.validateState();
        return u;
    }

    public void applyUpdate(
            String nome,
            String email,
            String login,
            String telefone,
            TipoUsuario tipoUsuario,
            List<Endereco> novosEnderecos) {
        if (nome != null)
            this.setNome(nome);
        if (email != null)
            this.setEmail(email);
        if (login != null)
            this.setLogin(login);
        if (telefone != null)
            this.setTelefone(telefone);
        if (tipoUsuario != null)
            this.setTipoUsuario(tipoUsuario);
        if (novosEnderecos != null)
            this.replaceEnderecos(novosEnderecos);
        validateState();
    }

    public void atualizarSenha(String novaSenha) {
        if (isBlank(novaSenha)) {
            throw new DomainValidationException("novaSenha é obrigatória");
        }
        if (novaSenha.length() < 8 || novaSenha.length() > 72) {
            throw new DomainValidationException("novaSenha deve ter entre 8 e 72 caracteres");
        }
        this.senha = novaSenha;
        validateState();
    }

    public void desativar() {
        this.ativo = Boolean.FALSE;
    }

    public void reativar() {
        this.ativo = Boolean.TRUE;
        validateState();
    }

    public List<Endereco> getEnderecos() {
        return enderecos == null ? Collections.emptyList() : Collections.unmodifiableList(enderecos);
    }

    private void replaceEnderecos(List<Endereco> novosEnderecos) {
        if (novosEnderecos == null || novosEnderecos.isEmpty()) {
            throw new DomainValidationException("ao menos um endereço deve ser informado");
        }

        novosEnderecos.forEach(Endereco::validateState);

        long principalAtivos = novosEnderecos.stream()
                .filter(e -> Boolean.TRUE.equals(e.getAtivo()))
                .filter(e -> Boolean.TRUE.equals(e.getPrincipal()))
                .count();
        if (principalAtivos != 1) {
            throw new DomainValidationException("exatamente um endereço principal ativo deve ser informado");
        }
        this.enderecos = new ArrayList<>(novosEnderecos);
    }

    private void validateState() {
        if (isBlank(nome))
            throw new DomainValidationException("nome é obrigatório");
        if (isBlank(email))
            throw new DomainValidationException("email é obrigatório");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainValidationException("email inválido");
        }
        if (isBlank(login))
            throw new DomainValidationException("login é obrigatório");
        if (!LOGIN_PATTERN.matcher(login).matches()) {
            throw new DomainValidationException("login inválido");
        }
        if (isBlank(senha))
            throw new DomainValidationException("senha é obrigatória");
        if (senha.length() < 8 || senha.length() > 72) {
            throw new DomainValidationException("senha deve ter entre 8 e 72 caracteres");
        }
        if (tipoUsuario == null) {
            throw new DomainValidationException("tipoUsuario é obrigatório");
        }
        if (enderecos == null || enderecos.isEmpty()) {
            throw new DomainValidationException("ao menos um endereço deve ser informado");
        }

        long principalAtivos = enderecos.stream()
                .filter(e -> Boolean.TRUE.equals(e.getAtivo()))
                .filter(e -> Boolean.TRUE.equals(e.getPrincipal()))
                .count();
        if (principalAtivos != 1) {
            throw new DomainValidationException("exatamente um endereço principal ativo deve ser informado");
        }
        if (ativo == null)
            ativo = Boolean.TRUE;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
