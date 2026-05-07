package com.restauranthub.restaurant_user_api.domain;

import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapio {

    private Long id;
    private Long restauranteId;
    private String restauranteNome;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Boolean apenasNoLocal;
    private String caminhoFoto;
    private Boolean ativo;
    private Instant createdAt;
    private Instant updatedAt;

    public static ItemCardapio create(
            Long restauranteId,
            String nome,
            String descricao,
            BigDecimal preco,
            Boolean apenasNoLocal,
            String caminhoFoto) {
        ItemCardapio item = new ItemCardapio();
        item.setRestauranteId(restauranteId);
        item.setNome(nome);
        item.setDescricao(descricao);
        item.setPreco(preco);
        item.setApenasNoLocal(apenasNoLocal);
        item.setCaminhoFoto(caminhoFoto);
        item.setAtivo(Boolean.TRUE);
        item.validateState();
        return item;
    }

    public void applyUpdate(
            String nome,
            String descricao,
            BigDecimal preco,
            Boolean apenasNoLocal,
            String caminhoFoto,
            Boolean ativo) {
        if (nome != null) this.nome = nome;
        if (descricao != null) this.descricao = descricao;
        if (preco != null) this.preco = preco;
        if (apenasNoLocal != null) this.apenasNoLocal = apenasNoLocal;
        if (caminhoFoto != null) this.caminhoFoto = caminhoFoto;
        if (ativo != null) this.ativo = ativo;
        validateState();
    }

    public void validateState() {
        if (restauranteId == null) throw new DomainValidationException("restauranteId é obrigatório");
        if (nome == null || nome.isBlank()) throw new DomainValidationException("nome do item é obrigatório");
        if (descricao == null || descricao.isBlank()) throw new DomainValidationException("descrição do item é obrigatória");
        if (preco == null || preco.signum() < 0) throw new DomainValidationException("preço do item deve ser maior ou igual a zero");
        if (apenasNoLocal == null) apenasNoLocal = Boolean.FALSE;
        if (ativo == null) ativo = Boolean.TRUE;
    }
}
