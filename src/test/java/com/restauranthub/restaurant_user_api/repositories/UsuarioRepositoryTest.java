package com.restauranthub.restaurant_user_api.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@EntityScan("com.restauranthub.restaurant_user_api.entities")
@EnableJpaRepositories("com.restauranthub.restaurant_user_api.repositories")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(UsuarioRepositoryTest.AuditingTestConfig.class)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void findByEmailIgnoreCase_deveEncontrarUsuario() {
        UsuarioEntity entity = novoUsuario("Joao", "joao@email.com", "joao.login");
        repository.saveAndFlush(entity);

        assertTrue(repository.findByEmailIgnoreCase("JOAO@EMAIL.COM").isPresent());
    }

    @Test
    void findByNomeContainingIgnoreCase_devePaginarResultados() {
        repository.saveAllAndFlush(List.of(
                novoUsuario("Maria Silva", "maria@email.com", "maria.login"),
                novoUsuario("Mario Souza", "mario@email.com", "mario.login")
        ));

        var page = repository.findByNomeContainingIgnoreCase("mar", PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        assertEquals("Maria Silva", page.getContent().get(0).getNome());
    }

    @Test
    void deveLancarErroQuandoEmailDuplicado() {
        repository.saveAndFlush(novoUsuario("Fulano", "duplicado@email.com", "fulano.login"));

        assertThrows(DataIntegrityViolationException.class, () ->
                repository.saveAndFlush(novoUsuario("Outro", "duplicado@email.com", "outro.login")));
    }

    private UsuarioEntity novoUsuario(String nome, String email, String login) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha("Senha@123");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setAtivo(true);

        EnderecoEntity end = new EnderecoEntity();
        end.setRua("Rua A");
        end.setNumero("123");
        end.setCidade("São Paulo");
        end.setEstado("SP");
        end.setCep("01001-000");
        end.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        end.setPrincipal(true);
        end.setAtivo(true);
        end.setUsuario(usuario);
        usuario.getEnderecos().add(end);
        return usuario;
    }

    @Configuration
    @EnableJpaAuditing
    static class AuditingTestConfig {
        // habilita auditoria para popular campos createdAt/updatedAt nos testes
    }
}
