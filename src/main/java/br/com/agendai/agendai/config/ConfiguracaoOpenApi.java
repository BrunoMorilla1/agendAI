package br.com.agendai.agendai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI apiPersonalizada() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Lista de Tarefas")
                        .description("Sistema de Gerenciamento de Tarefas desenvolvido com Spring Boot para fins de demonstração em entrevistas")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bruno Morilla")
                                .email("morilla.bsm@gmail.com")
                                .url("https://github.com/dev/todo-list-api"))
                        .license(new License()
                                .name("Licença MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}