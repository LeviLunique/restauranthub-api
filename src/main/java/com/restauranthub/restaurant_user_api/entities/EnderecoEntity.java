package com.restauranthub.restaurant_user_api.entities;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "endereco")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class EnderecoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco", nullable = false, length = 20)
    private TipoEndereco tipoEndereco;

    @Column(nullable = false, length = 255)
    private String rua;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false)
    private Boolean principal = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "data_atualizacao", nullable = false)
    private Instant updatedAt;
}
