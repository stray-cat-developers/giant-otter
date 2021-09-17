package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.domain.crawler.SwaggerDocCacheStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.ArrayList

@EnableSwagger2
@Configuration
class SwaggerConfiguration {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Primary
    @Bean
    @Lazy
    fun swaggerResourcesProvider(
        defaultResourcesProvider: InMemorySwaggerResourcesProvider,
        temp: RestTemplate
    ): SwaggerResourcesProvider {
        return SwaggerResourcesProvider {
            val resources = ArrayList(defaultResourcesProvider.get())
            resources.clear()
            resources.addAll(SwaggerDocCacheStore.findAll().map { it.first }.sortedBy { it.name })
            resources
        }
    }

    @Bean
    fun baseDocket(): Docket = Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("io.mustelidae.grantotter.domain"))
        .build()
}
