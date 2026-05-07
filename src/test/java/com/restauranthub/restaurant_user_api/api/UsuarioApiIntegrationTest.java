package com.restauranthub.restaurant_user_api.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.repositories.UsuarioRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:inttest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        // 32 bytes base64 -> "12345678901234567890123456789012"
        "security.jwt.secret=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=",
        "security.jwt.expiration-ms=3600000"
})
class UsuarioApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        usuarioRepository.flush();
        usuarioRepository.saveAndFlush(adminUsuario());
    }

    @Test
    void fluxoCompleto_authECrudUsuario() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api/v1";

        // Login
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                Map.of("email", "admin@restauranthub.com", "senha", "Admin@123"),
                String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        JsonNode loginJson = objectMapper.readTree(loginResponse.getBody());
        String token = loginJson.get("token").asText();
        assertNotNull(token);
        assertTrue(token.length() > 20);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Criar usuário
        String novoUsuarioJson = """
                {
                  "nome": "Cliente API",
                  "email": "cliente.api@test.com",
                  "login": "cliente.api",
                  "senha": "Senha@123",
                  "tipoUsuario": "CLIENTE",
                  "enderecos": [
                    {
                      "tipoEndereco": "RESIDENCIAL",
                      "rua": "Rua das Flores",
                      "numero": "123",
                      "cidade": "São Paulo",
                      "estado": "SP",
                      "cep": "01001-000",
                      "principal": true
                    }
                  ]
                }
                """;

        ResponseEntity<String> createResponse = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.POST,
                new HttpEntity<>(novoUsuarioJson, headers),
                String.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        JsonNode created = objectMapper.readTree(createResponse.getBody());
        long createdId = created.get("id").asLong();
        assertEquals("Cliente API", created.get("nome").asText());

        // Buscar usuário criado
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/users/" + createdId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        JsonNode fetched = objectMapper.readTree(getResponse.getBody());
        assertEquals(createdId, fetched.get("id").asLong());
        assertEquals("cliente.api@test.com", fetched.get("email").asText());
    }

    private UsuarioEntity adminUsuario() {
        UsuarioEntity admin = new UsuarioEntity();
        admin.setNome("Administrador");
        admin.setEmail("admin@restauranthub.com");
        admin.setLogin("admin");
        admin.setSenha(new BCryptPasswordEncoder().encode("Admin@123"));
        admin.setTipoUsuario(TipoUsuario.ADMIN);
        admin.setAtivo(true);

        EnderecoEntity end = new EnderecoEntity();
        end.setRua("Av. Central");
        end.setNumero("1000");
        end.setCidade("São Paulo");
        end.setEstado("SP");
        end.setCep("01001-000");
        end.setTipoEndereco(TipoEndereco.COMERCIAL);
        end.setPrincipal(true);
        end.setAtivo(true);
        end.setUsuario(admin);
        admin.setEnderecos(List.of(end));
        return admin;
    }
}
