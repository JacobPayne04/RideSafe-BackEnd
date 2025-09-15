package com.Jacob.ridesafebackend.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

  // 1) Base info + JWT bearer security (applies to all ops)
  @Bean
  public OpenAPI apiInfo() {
    return new OpenAPI()
        .info(new Info()
            .title("RideSafe API")
            .description("API documentation for RideSafe app")
            .version("v1"))
        .components(new Components().addSecuritySchemes(
            "bearerAuth",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }

  // 2) Add global standard responses once (no per-method clutter)
  @Bean
  public OpenApiCustomizer globalResponses() {
    return openApi -> openApi.getPaths().values().forEach(path ->
        path.readOperations().forEach(op -> {
          var r = op.getResponses();
          r.addApiResponse("400", new io.swagger.v3.oas.models.responses.ApiResponse().description("Bad Request"));
          r.addApiResponse("401", new io.swagger.v3.oas.models.responses.ApiResponse().description("Unauthorized"));
          r.addApiResponse("403", new io.swagger.v3.oas.models.responses.ApiResponse().description("Forbidden"));
          r.addApiResponse("404", new io.swagger.v3.oas.models.responses.ApiResponse().description("Not Found"));
          r.addApiResponse("500", new io.swagger.v3.oas.models.responses.ApiResponse().description("Server Error"));
        })
    );
  }
}
