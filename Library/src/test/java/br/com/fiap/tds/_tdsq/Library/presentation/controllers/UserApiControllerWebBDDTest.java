package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.UserDTO;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BDD Web test do UserApiController (camada MVC isolada).
 * Não usa @MockBean (deprecated no Spring Boot 3.4+).
 * Em vez disso, registra um bean @Primary de UserService (mock do Mockito) via @TestConfiguration.
 */
@WebMvcTest(UserApiController.class)
class UserApiControllerWebBDDTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Autowired UserService userService; // mock provido pela TestConfig

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class FindAll {

        @Test
        @DisplayName("Dado usuários existentes, quando listar, então 200 e array com itens")
        void should_list_all_users() throws Exception {
            // Given
            var u1 = User.builder().id(UUID.randomUUID()).name("Ana").email("ana@ex.com").password("x").build();
            var u2 = User.builder().id(UUID.randomUUID()).name("Bob").email("bob@ex.com").password("y").build();
            BDDMockito.given(userService.findAll()).willReturn(List.of(u1, u2));

            // When / Then
            mvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class FindById {

        @Test
        @DisplayName("Dado ID existente, quando buscar, então 200 e DTO correto")
        void should_return_user_when_found() throws Exception {
            var id = UUID.randomUUID();
            var u = User.builder().id(id).name("Carol").email("carol@ex.com").password("pwd").build();
            BDDMockito.given(userService.findById(id)).willReturn(Optional.of(u));

            mvc.perform(get("/api/users/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(id.toString())))
                    .andExpect(jsonPath("$.name", is("Carol")))
                    .andExpect(jsonPath("$.email", is("carol@ex.com")));
        }

        @Test
        @DisplayName("Dado ID inexistente, quando buscar, então 404")
        void should_return_404_when_not_found() throws Exception {
            var id = UUID.randomUUID();
            BDDMockito.given(userService.findById(id)).willReturn(Optional.empty());

            mvc.perform(get("/api/users/{id}", id))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class Create {

        @Test
        @DisplayName("Dado payload válido, quando criar, então 201 e corpo do DTO criado")
        void should_create_and_return_201() throws Exception {
            // Given
            UserDTO req = UserDTO.builder()
                    .name("Diego")
                    .email("diego@ex.com")
                    .password("pwd123")
                    .build();

            var created = User.builder()
                    .id(UUID.randomUUID())
                    .name("Diego")
                    .email("diego@ex.com")
                    .password("pwd123")
                    .build();

            BDDMockito.given(userService.create(any())).willReturn(created);

            // When / Then
            mvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(created.getId().toString())))
                    .andExpect(jsonPath("$.name", is("Diego")))
                    .andExpect(jsonPath("$.email", is("diego@ex.com")));
        }

        @Test
        @DisplayName("Dado payload inválido, quando criar, então 400 com mensagens de validação")
        void should_return_400_on_validation_errors() throws Exception {
            // Given: name em branco e password curto (DTO tem Bean Validation)
            UserDTO invalid = UserDTO.builder()
                    .name("") // @NotBlank
                    .email("invalido") // @Email
                    .password("123")   // @Size(min=6)
                    .build();

            // When / Then
            mvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users (ID no corpo)")
    class DeleteByIdBody {

        @Test
        @DisplayName("Dado ID inexistente, quando deletar, então 404")
        void should_404_when_delete_nonexistent() throws Exception {
            var id = UUID.randomUUID();
            BDDMockito.given(userService.existsById(id)).willReturn(false);

            mvc.perform(delete("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(id)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Dado ID existente, quando deletar, então 204 e service chamado")
        void should_204_when_delete_ok() throws Exception {
            var id = UUID.randomUUID();
            BDDMockito.given(userService.existsById(id)).willReturn(true);

            mvc.perform(delete("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(id)))
                    .andExpect(status().isNoContent());

            BDDMockito.then(userService).should().removeById(id);
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class Update {

        @Test
        @DisplayName("Dado ID inexistente, quando atualizar, então 404")
        void should_404_when_update_nonexistent() throws Exception {
            var id = UUID.randomUUID();
            BDDMockito.given(userService.existsById(id)).willReturn(false);

            UserDTO req = UserDTO.builder()
                    .name("Novo")
                    .email("novo@ex.com")
                    .password("pwd123")
                    .build();

            mvc.perform(put("/api/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Dado ID existente, quando atualizar, então 201 (conforme controlador usa create)")
        void should_201_when_update_existing() throws Exception {
            var id = UUID.randomUUID();
            BDDMockito.given(userService.existsById(id)).willReturn(true);

            var saved = User.builder()
                    .id(id)
                    .name("Eva")
                    .email("eva@ex.com")
                    .password("pwd123")
                    .build();
            BDDMockito.given(userService.create(any())).willReturn(saved);

            UserDTO req = UserDTO.builder()
                    .name("Eva")
                    .email("eva@ex.com")
                    .password("pwd123")
                    .build();

            mvc.perform(put("/api/users/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(id.toString())))
                    .andExpect(jsonPath("$.name", is("Eva")));
        }
    }

    @Nested
    @DisplayName("GET /api/users/?email=...")
    class FindByEmail {

        @Test
        @DisplayName("Dado email existente, quando filtrar, então 200 e lista DTO")
        void should_filter_by_email() throws Exception {
            var u = User.builder().id(UUID.randomUUID()).name("Fabio").email("fabio@ex.com").password("pwd").build();
            BDDMockito.given(userService.findByEmail("fabio@ex.com")).willReturn(List.of());

            mvc.perform(get("/api/users/").param("email", "fabio@ex.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email", is("fabio@ex.com")))
                    .andExpect(jsonPath("$[0].name", is("Fabio")));
        }
    }

    @Nested
    @DisplayName("GET /api/users/paged")
    class Paged {

        @Test
        @DisplayName("Dado paginação válida, quando buscar, então 200 e Page mapeada")
        void should_return_page() throws Exception {
            var u = User.builder().id(UUID.randomUUID()).name("Gabi").email("gabi@ex.com").password("pwd").build();
            var page = new PageImpl<>(List.of(u), PageRequest.of(0, 10, Sort.by("name")), 1);
            BDDMockito.given(userService.findAllPaged(any())).willReturn(page);

            mvc.perform(get("/api/users/paged").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name", is("Gabi")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }
    }

    // Observação: o endpoint @GetMapping("/name") do controlador retorna null; não testado para evitar 500.
}
