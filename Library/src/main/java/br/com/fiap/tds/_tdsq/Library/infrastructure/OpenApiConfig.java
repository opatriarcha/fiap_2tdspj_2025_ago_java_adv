package br.com.fiap.tds._tdsq.Library.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info( new Info()
                        .title("Api de exemplo")
                        .version("1.0")
                        .description("API do curso de adv java no 2tdsAgo.")
                );

    }
}
