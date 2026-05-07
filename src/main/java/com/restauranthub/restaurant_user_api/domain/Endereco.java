package com.restauranthub.restaurant_user_api.domain;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
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
public class Endereco {

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
    private Instant createdAt;
    private Instant updatedAt;

    public static Endereco create(
            TipoEndereco tipoEndereco,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            Boolean principal,
            Boolean ativo
    ) {
        Endereco e = new Endereco();
        e.setTipoEndereco(tipoEndereco);
        e.setRua(rua);
        e.setNumero(numero);
        e.setComplemento(complemento);
        e.setBairro(bairro);
        e.setCidade(cidade);
        e.setEstado(estado);
        e.setCep(cep);
        e.setPrincipal(principal != null ? principal : Boolean.FALSE);
        e.setAtivo(ativo != null ? ativo : Boolean.TRUE);
        e.validateState();
        return e;
    }

    public void applyUpdate(
            TipoEndereco tipoEndereco,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            Boolean principal,
            Boolean ativo
    ) {
        if (tipoEndereco != null) this.setTipoEndereco(tipoEndereco);
        if (rua != null) this.setRua(rua);
        if (numero != null) this.setNumero(numero);
        if (complemento != null) this.setComplemento(complemento);
        if (bairro != null) this.setBairro(bairro);
        if (cidade != null) this.setCidade(cidade);
        if (estado != null) this.setEstado(estado);
        if (cep != null) this.setCep(cep);
        if (principal != null) this.setPrincipal(principal);
        if (ativo != null) this.setAtivo(ativo);
        validateState();
    }

    public void validateState() {
        if (tipoEndereco == null) throw new DomainValidationException("tipoEndereco é obrigatório");
        if (isBlank(rua)) throw new DomainValidationException("rua é obrigatória");
        if (isBlank(cidade)) throw new DomainValidationException("cidade é obrigatória");
        if (isBlank(cep)) throw new DomainValidationException("cep é obrigatório");
        if (cep != null && !cep.matches("\\d{5}-?\\d{3}")) {
            throw new DomainValidationException("cep deve seguir o formato 00000-000");
        }
        if (estado != null && !estado.matches("^[A-Za-z]{2}$")) {
            throw new DomainValidationException("estado deve conter a sigla de 2 letras");
        }
        if (principal == null) throw new DomainValidationException("principal é obrigatório");
        if (ativo == null) throw new DomainValidationException("ativo é obrigatório");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
