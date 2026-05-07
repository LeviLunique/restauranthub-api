package com.restauranthub.restaurant_user_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "item_cardapio")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class ItemCardapioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private RestauranteEntity restaurante;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "apenas_no_local", nullable = false)
    private Boolean apenasNoLocal = Boolean.FALSE;

    @Column(name = "caminho_foto", length = 255)
    private String caminhoFoto;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "data_atualizacao", nullable = false)
    private Instant updatedAt;
}
