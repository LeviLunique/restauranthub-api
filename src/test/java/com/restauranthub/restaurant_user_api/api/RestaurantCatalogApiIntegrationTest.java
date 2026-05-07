package com.restauranthub.restaurant_user_api.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restauranthub.restaurant_user_api.domain.enums.TipoEndereco;
import com.restauranthub.restaurant_user_api.domain.enums.TipoUsuario;
import com.restauranthub.restaurant_user_api.entities.EnderecoEntity;
import com.restauranthub.restaurant_user_api.entities.TipoUsuarioCadastroEntity;
import com.restauranthub.restaurant_user_api.entities.UsuarioEntity;
import com.restauranthub.restaurant_user_api.repositories.TipoUsuarioCadastroRepository;
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
        "spring.datasource.url=jdbc:h2:mem:phase2;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "security.jwt.secret=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=",
        "security.jwt.expiration-ms=3600000"
})
class RestaurantCatalogApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoUsuarioCadastroRepository tipoUsuarioCadastroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long ownerId;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();
        tipoUsuarioCadastroRepository.deleteAll();
        TipoUsuarioCadastroEntity adminType = tipo("ADMIN");
        TipoUsuarioCadastroEntity ownerType = tipo("DONO_RESTAURANTE");
        var savedTypes = tipoUsuarioCadastroRepository.saveAll(List.of(adminType, ownerType));
        TipoUsuarioCadastroEntity savedAdminType = savedTypes.stream().filter(t -> "ADMIN".equals(t.getNome())).findFirst().orElseThrow();
        TipoUsuarioCadastroEntity savedOwnerType = savedTypes.stream().filter(t -> "DONO_RESTAURANTE".equals(t.getNome())).findFirst().orElseThrow();
        usuarioRepository.save(adminUsuario(savedAdminType));
        ownerId = usuarioRepository.save(ownerUsuario(savedOwnerType)).getId();
    }

    @Test
    void fluxoFase2_loginTipoRestauranteECardapio() throws Exception {
        String baseUrl = "http://localhost:" + port + "/api/v1";

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                Map.of("email", "admin@restauranthub.com", "senha", "Admin@123"),
                String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String token = objectMapper.readTree(loginResponse.getBody()).get("token").asText();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<String> typesResponse = restTemplate.exchange(
                baseUrl + "/user-types",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        assertEquals(HttpStatus.OK, typesResponse.getStatusCode());

        String restauranteJson = """
                {
                  "nome": "Casa da Fase 2",
                  "tipoCozinha": "Brasileira",
                  "horarioFuncionamento": "Seg-Dom 11:00-23:00",
                  "donoUsuarioId": %d,
                  "rua": "Rua dos Sabores",
                  "numero": "200",
                  "bairro": "Centro",
                  "cidade": "São Paulo",
                  "estado": "SP",
                  "cep": "01001-000"
                }
                """.formatted(ownerId);

        ResponseEntity<String> createRestaurante = restTemplate.exchange(
                baseUrl + "/restaurants",
                HttpMethod.POST,
                new HttpEntity<>(restauranteJson, headers),
                String.class);
        assertEquals(HttpStatus.CREATED, createRestaurante.getStatusCode());
        JsonNode restauranteBody = objectMapper.readTree(createRestaurante.getBody());
        long restauranteId = restauranteBody.get("id").asLong();

        String itemJson = """
                {
                  "nome": "Prato Executivo",
                  "descricao": "Arroz, feijão e proteína do dia",
                  "preco": 29.90,
                  "apenasNoLocal": false,
                  "caminhoFoto": "/images/prato-executivo.jpg"
                }
                """;

        ResponseEntity<String> createItem = restTemplate.exchange(
                baseUrl + "/restaurants/" + restauranteId + "/menu-items",
                HttpMethod.POST,
                new HttpEntity<>(itemJson, headers),
                String.class);
        assertEquals(HttpStatus.CREATED, createItem.getStatusCode());

        ResponseEntity<String> listItems = restTemplate.exchange(
                baseUrl + "/restaurants/" + restauranteId + "/menu-items",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        assertEquals(HttpStatus.OK, listItems.getStatusCode());
        assertEquals(1, objectMapper.readTree(listItems.getBody()).size());
    }

    private TipoUsuarioCadastroEntity tipo(String nome) {
        TipoUsuarioCadastroEntity entity = new TipoUsuarioCadastroEntity();
        entity.setNome(nome);
        entity.setDescricao(nome);
        entity.setAtivo(true);
        return entity;
    }

    private UsuarioEntity adminUsuario(TipoUsuarioCadastroEntity tipo) {
        UsuarioEntity admin = new UsuarioEntity();
        admin.setNome("Administrador");
        admin.setEmail("admin@restauranthub.com");
        admin.setLogin("admin");
        admin.setSenha(new BCryptPasswordEncoder().encode("Admin@123"));
        admin.setTipoUsuario(TipoUsuario.ADMIN);
        admin.setTipoUsuarioCadastro(tipo);
        admin.setAtivo(true);
        admin.setEnderecos(List.of(endereco(admin, TipoEndereco.COMERCIAL)));
        return admin;
    }

    private UsuarioEntity ownerUsuario(TipoUsuarioCadastroEntity tipo) {
        UsuarioEntity owner = new UsuarioEntity();
        owner.setNome("Roberto");
        owner.setEmail("roberto@restaurante.com");
        owner.setLogin("roberto");
        owner.setSenha(new BCryptPasswordEncoder().encode("Dono@123"));
        owner.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);
        owner.setTipoUsuarioCadastro(tipo);
        owner.setAtivo(true);
        owner.setEnderecos(List.of(endereco(owner, TipoEndereco.COMERCIAL)));
        return owner;
    }

    private EnderecoEntity endereco(UsuarioEntity usuario, TipoEndereco tipoEndereco) {
        EnderecoEntity endereco = new EnderecoEntity();
        endereco.setUsuario(usuario);
        endereco.setTipoEndereco(tipoEndereco);
        endereco.setRua("Rua A");
        endereco.setNumero("10");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01001-000");
        endereco.setPrincipal(true);
        endereco.setAtivo(true);
        return endereco;
    }
}
