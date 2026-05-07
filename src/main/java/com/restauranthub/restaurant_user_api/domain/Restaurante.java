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
public class Restaurante {

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
    private Instant createdAt;
    private Instant updatedAt;

    public static Restaurante create(
            String nome,
            String tipoCozinha,
            String horarioFuncionamento,
            Long donoUsuarioId,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(nome);
        restaurante.setTipoCozinha(tipoCozinha);
        restaurante.setHorarioFuncionamento(horarioFuncionamento);
        restaurante.setDonoUsuarioId(donoUsuarioId);
        restaurante.setRua(rua);
        restaurante.setNumero(numero);
        restaurante.setComplemento(complemento);
        restaurante.setBairro(bairro);
        restaurante.setCidade(cidade);
        restaurante.setEstado(estado);
        restaurante.setCep(cep);
        restaurante.setAtivo(Boolean.TRUE);
        restaurante.validateState();
        return restaurante;
    }

    public void applyUpdate(
            String nome,
            String tipoCozinha,
            String horarioFuncionamento,
            Long donoUsuarioId,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            Boolean ativo) {
        if (nome != null) this.nome = nome;
        if (tipoCozinha != null) this.tipoCozinha = tipoCozinha;
        if (horarioFuncionamento != null) this.horarioFuncionamento = horarioFuncionamento;
        if (donoUsuarioId != null) this.donoUsuarioId = donoUsuarioId;
        if (rua != null) this.rua = rua;
        if (numero != null) this.numero = numero;
        if (complemento != null) this.complemento = complemento;
        if (bairro != null) this.bairro = bairro;
        if (cidade != null) this.cidade = cidade;
        if (estado != null) this.estado = estado;
        if (cep != null) this.cep = cep;
        if (ativo != null) this.ativo = ativo;
        validateState();
    }

    public void validateState() {
        if (nome == null || nome.isBlank()) throw new DomainValidationException("nome do restaurante é obrigatório");
        if (tipoCozinha == null || tipoCozinha.isBlank()) throw new DomainValidationException("tipo de cozinha é obrigatório");
        if (horarioFuncionamento == null || horarioFuncionamento.isBlank()) throw new DomainValidationException("horário de funcionamento é obrigatório");
        if (donoUsuarioId == null) throw new DomainValidationException("donoUsuarioId é obrigatório");
        if (rua == null || rua.isBlank()) throw new DomainValidationException("rua é obrigatória");
        if (cidade == null || cidade.isBlank()) throw new DomainValidationException("cidade é obrigatória");
        if (cep == null || cep.isBlank()) throw new DomainValidationException("cep é obrigatório");
        if (ativo == null) ativo = Boolean.TRUE;
    }
}
