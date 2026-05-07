package com.restauranthub.restaurant_user_api.domain;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnderecoTest {

    @Test
    void deveValidarCepFormatoCorreto() {
        assertDoesNotThrow(() -> Endereco.create(
                TipoEndereco.RESIDENCIAL,
                "Rua A",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01001-000",
                true,
                true
        ));
    }

    @Test
    void deveLancarExcecaoQuandoCepInvalido() {
        assertThrows(DomainValidationException.class, () -> Endereco.create(
                TipoEndereco.RESIDENCIAL,
                "Rua A",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "0100A-000",
                true,
                true
        ));
    }

    @Test
    void deveLancarExcecaoQuandoEstadoComTamanhoInvalido() {
        assertThrows(DomainValidationException.class, () -> Endereco.create(
                TipoEndereco.RESIDENCIAL,
                "Rua A",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SPA",
                "01001-000",
                true,
                true
        ));
    }
}
