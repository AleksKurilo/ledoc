package dk.ledocsystem.api.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import dk.ledocsystem.api.exceptions.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
@Import({ BeanValidatorPluginsConfiguration.class })
public class SwaggerConfig {
    private static final String LEDOC_BASE_PACKAGE = "dk.ledocsystem.ledoc";

    @Value("${build.version}")
    private String version;

    private final TypeResolver typeResolver;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(LEDOC_BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .groupName(LEDOC_BASE_PACKAGE)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, getGlobalResponse())
                .globalResponseMessage(RequestMethod.POST, postGlobalResponse())
                .globalResponseMessage(RequestMethod.PUT, putGlobalResponse())
                .globalResponseMessage(RequestMethod.DELETE, deleteGlobalResponse())
                .additionalModels(typeResolver.resolve(RestResponse.class))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Ledoc v4 API")
                .description("Documentation for Ledoc v4 API")
                .version(version)
                .contact(new Contact("CHI Software", "https://chisw.com", "info@chisw.com"))
                .build();
    }

    private List<ResponseMessage> getGlobalResponse() {
        return Collections.singletonList(get500GlobalResponse());
    }

    private List<ResponseMessage> deleteGlobalResponse() {
        return Collections.singletonList(get500GlobalResponse());
    }

    private List<ResponseMessage> postGlobalResponse() {
        return Arrays.asList(get400GlobalResponse(), get500GlobalResponse());
    }

    private List<ResponseMessage> putGlobalResponse() {
        return Arrays.asList(get400GlobalResponse(), get404GlobalResponse(), get500GlobalResponse());
    }

    private ResponseMessage get400GlobalResponse() {
        return new ResponseMessageBuilder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Bad request: validation failed")
                .responseModel(new ModelRef("RestResponse"))
                .build();
    }

    private ResponseMessage get404GlobalResponse() {
        return new ResponseMessageBuilder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("Entity not found")
                .responseModel(new ModelRef("RestResponse"))
                .build();
    }

    private ResponseMessage get500GlobalResponse() {
        return new ResponseMessageBuilder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .responseModel(new ModelRef("RestResponse"))
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("apiKey",
                authorizationScopes));
    }
}
