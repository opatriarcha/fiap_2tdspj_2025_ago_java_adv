package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.domainmodel.AuthUser;
import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepositoryImpl;
import br.com.fiap.tds._tdsq.Library.infrastructure.JwtAuthFilter;
import br.com.fiap.tds._tdsq.Library.infrastructure.config.JwtHelper;
import br.com.fiap.tds._tdsq.Library.service.UserService;
import br.com.fiap.tds._tdsq.Library.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
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
        void shouldReturnUSerWhenFound() throws Exception {
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
            User loginUser = new User(
                    null,
                    "user",
                    "user@gmail.com",
                    passwordEncoder.encode("0123456789")
            );

            AuthUser authUser = new AuthUser( loginUser);

            var token = jwtHelper.generateToken(authUser);

            mockMvc.perform(get("/api/users/{id}", id)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect((jsonPath("$.id", is(id.toString()))))
                    .andExpect(jsonPath("$.name", is("JUSE")))
                    .andExpect(jsonPath("$.email", is("juse.lascado@gmail.com")));

        }
    }



}
