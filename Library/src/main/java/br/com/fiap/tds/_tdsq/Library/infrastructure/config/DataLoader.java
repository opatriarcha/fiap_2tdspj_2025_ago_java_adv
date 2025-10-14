package br.com.fiap.tds._tdsq.Library.infrastructure.config;

import br.com.fiap.tds._tdsq.Library.domainmodel.User;
import br.com.fiap.tds._tdsq.Library.domainmodel.repositories.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            List<User> users = new LinkedList<>();
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {

                User user = new User(
                        null,
                        faker.name().fullName(),
                        faker.internet().emailAddress(),
                        faker.internet().password(8, 16)
                );
                System.out.println(user);
                users.add(user);
            }

            User loginUser = new User(
                    null,
                    "user",
                    "user@gmail.com",
                    "0123456789"
            );
            users.add(loginUser);
            userRepository.saveAll(users);
        };
    }
}
