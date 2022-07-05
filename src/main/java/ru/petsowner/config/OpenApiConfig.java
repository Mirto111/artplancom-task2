package ru.petsowner.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигуратор Swagger-а.
 */

@Configuration
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
@OpenAPIDefinition(
    info = @Info(
        title = "REST API documentation",
        version = "1.0",
        description = "Приложение для владельцев животных"
    ),
    security = @SecurityRequirement(name = "basicAuth")
)
public class OpenApiConfig {

  //    https://ru.stackoverflow.com/a/1276885/209226
  static {
    var schema = new Schema<LocalTime>();
    schema.example(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, schema);
  }

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("REST API")
        .pathsToMatch("/api/**")
        .build();
  }
}
