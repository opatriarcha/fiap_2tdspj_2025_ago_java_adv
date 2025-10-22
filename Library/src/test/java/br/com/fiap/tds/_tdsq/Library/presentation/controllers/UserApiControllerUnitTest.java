package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.domainmodel.AuthUser;
import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.infrastructure.config.JwtHelper;
import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.UserDTO;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserApiControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserDTO invalidUser = UserDTO.builder()
            .name("")
            .email("invalido")
            .password("")
            .build();




    @TestConfiguration
    static class TestConfig{

        @Bean
        @Primary
        UserService userService(){
            return Mockito.mock(UserService.class);
        }

        @Bean
        @Primary
        JwtHelper jwtHelper(){
            return new JwtHelper();

        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class FindByID{

        @Test
        @DisplayName(" Dado um id existente, quando buscar, então retorna 200 e o DTO correto")
        void shouldReturnUserWhenFound() throws Exception {
            var id = UUID.randomUUID();
            var user = User.builder()
                    .id(id)
                    .name("JUSE")
                    .email("juse.lascado@gmail.com")
                    .password("12345678")
                    .build();

            BDDMockito
                    .given(userService.findById(id))
                    .willReturn(Optional.of(user));
            var token = buildUthUserAndToken();

            mockMvc.perform(get("/api/users/{id}", id)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect((jsonPath("$.id", is(id.toString()))))
                    .andExpect(jsonPath("$.name", is("JUSE")))
                    .andExpect(jsonPath("$.email", is("juse.lascado@gmail.com")));

        }

        @Test
        @DisplayName(" Dado um ID inexistente, quando realizar a busca, retornar codigo 404.")
        void should_return_404_when_user_not_found()throws Exception{
            var id = UUID.randomUUID();

            BDDMockito.given(userService.findById(id))
                    .willReturn(Optional.empty());

            var token = buildUthUserAndToken();
            mockMvc.perform(
                    get("/api/users/{id}", id)
                            .header("Authorization", "Bearer " + token)
            )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateUser{
        @Test
        @DisplayName("Dado payload válido, cria o objeto, retorna 201 com o DTO do novo objeto no corpo da resposta")
        void should_create_and_return_201() throws Exception {
            //Given
            var reg = UserDTO.toEntity(UserDTO.builder()
                    .name("Dirego")
                    .email("dirego@flex.com")
                    .password("pwd124")
                    .build());

            var created = UserDTO.builder()
                    .id(UUID.randomUUID())
                    .name("Dirego")
                    .email("dirego@flex.com")
                    .password("pwd124")
                    .build();

            BDDMockito.given(userService.create(reg)).willReturn(UserDTO.toEntity(created));

            mockMvc.perform(
                    post("/api/users")
                    .header("Authorization", "Bearer " + buildUthUserAndToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reg))
            )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(created.getId().toString())))
                    .andExpect(jsonPath("$.name", is(created.getName())))
                    .andExpect(jsonPath("$.email", is(created.getEmail())))
                    .andExpect(jsonPath("$.password", is(created.getPassword())));
        }

        @Test
        @DisplayName("Dado payload inválido, naõa cria o objeto, retorna 400 com mensagens de validação")
        void shouldnot_create_and_return_400() throws Exception {

            mockMvc.perform(
                    post("/api/users")
                            .header("Authorization", "Bearer " + buildUthUserAndToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUser)))
                    .andExpect(status().isBadRequest());
        }
    }

    private String buildUthUserAndToken() {
        User authUser = buildAuthUser();
        var token = buildToken(authUser);
        return token;
    }

    private String buildToken(User authUser) {
        return jwtHelper.generateToken(new AuthUser(authUser));
    }

    private User buildAuthUser() {
        var idAuthUser = UUID.randomUUID();
        return new User(
                idAuthUser,
                "user",
                "user@gmail.com",
                passwordEncoder.encode("0123456789")
        );

    }
}
