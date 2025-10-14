package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.UserDTO;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserApiControllerIntegrationBDDIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserService userService;

    UUID existingId;

    @BeforeEach
    void seed() {
        var created = userService.create(
                br.com.fiap.tds._tdsq.Library.domainmodel.User.builder()
                        .name("Zara")
                        .email("zara@ex.com")
                        .password("pwd123")
                        .build()
        );
        existingId = created.getId();
    }

    @Test
    @DisplayName("Fluxo CRUD completo")
    void crud_flow() throws Exception {
        // GET all
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Zara")));

        // GET by id
        mvc.perform(get("/api/users/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("zara@ex.com")));

        // PUT update
        UserDTO updateReq = UserDTO.builder()
                .name("Zara Atualizada")
                .email("zara@ex.com")
                .password("pwd456")
                .build();

        mvc.perform(put("/api/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Zara Atualizada")));

        // DELETE
        mvc.perform(delete("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingId)))
                .andExpect(status().isNoContent());

        // GET by id após exclusão
        mvc.perform(get("/api/users/{id}", existingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST cria e GET /?email retorna filtrado")
    void post_and_filter_by_email() throws Exception {
        UserDTO req = UserDTO.builder()
                .name("Lia")
                .email("lia@ex.com")
                .password("pwd123")
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("lia@ex.com")));

        mvc.perform(get("/api/users/").param("email", "lia@ex.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Lia")));
    }
}

