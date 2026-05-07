package com.restauranthub.restaurant_user_api.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;

class UsuarioTest {

    private Endereco enderecoResidencial() {
        return Endereco.create(
                TipoEndereco.RESIDENCIAL,
                "Rua A",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01001-000",
                true,
                true);
    }

    private Endereco enderecoComercialPrincipalFalse() {
        return Endereco.create(
                TipoEndereco.COMERCIAL,
                "Av. B",
                "999",
                "Conj 10",
                "Bela Vista",
                "São Paulo",
                "SP",
                "01310-100",
                false,
                true);
    }

    @Test
    void deveCriarUsuarioValidoComUmEnderecoPrincipal() {
        Endereco end = enderecoResidencial();
        assertDoesNotThrow(() -> Usuario.create(
                "Fulano",
                "fulano@email.com",
                "fulano.login",
                "Senha@123",
                TipoUsuario.CLIENTE,
                "+55 11 99999-9999",
                List.of(end)));
    }

    @Test
    void naoDeveCriarUsuarioSemEndereco() {
        assertThrows(DomainValidationException.class, () -> Usuario.create(
                "Fulano",
                "fulano@email.com",
                "fulano.login",
                "Senha@123",
                TipoUsuario.CLIENTE,
                "+55 11 99999-9999",
                List.of()));
    }

    @Test
    void naoDevePermitirMaisDeUmEnderecoPrincipalAtivo() {
        Endereco end1 = enderecoResidencial();
        Endereco end2 = enderecoComercialPrincipalFalse();
        end2.setPrincipal(true);

        assertThrows(DomainValidationException.class, () -> Usuario.create(
                "Fulano",
                "fulano@email.com",
                "fulano.login",
                "Senha@123",
                TipoUsuario.CLIENTE,
                "+55 11 99999-9999",
                List.of(end1, end2)));
    }

    @Test
    void naoDevePermitirSenhaForaDoIntervalo() {
        Endereco end = enderecoResidencial();
        assertThrows(DomainValidationException.class, () -> Usuario.create(
                "Fulano",
                "fulano@email.com",
                "fulano.login",
                "curta",
                TipoUsuario.CLIENTE,
                "+55 11 99999-9999",
                List.of(end)));
    }
}
