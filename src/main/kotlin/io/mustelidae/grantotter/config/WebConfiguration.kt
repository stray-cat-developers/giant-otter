package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.utils.Jackson
import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springdoc.core.providers.ActuatorProvider
import org.springdoc.webmvc.ui.SwaggerIndexTransformer
import org.springdoc.webmvc.ui.SwaggerResourceResolver
import org.springdoc.webmvc.ui.SwaggerWebMvcConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.support.FormattingConversionService
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.ResourceHttpMessageConverter
import org.springframework.http.converter.ResourceRegionHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import java.time.format.DateTimeFormatter
import java.util.Optional

@Configuration
@ControllerAdvice
class WebConfiguration(
    private val swaggerUiConfigParameters: SwaggerUiConfigParameters,
    private val swaggerIndexTransformer: SwaggerIndexTransformer,
    private val actuatorProvider: Optional<ActuatorProvider>,
    private val swaggerResourceResolver: SwaggerResourceResolver,
) : DelegatingWebMvcConfiguration() {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uiRootPath = StringBuilder()
        val swaggerPath = swaggerUiConfigParameters.path
        if (swaggerPath.contains("/")) {
            uiRootPath.append(swaggerPath, 0, swaggerPath.lastIndexOf("/"))
        }

        registry.addResourceHandler("$uiRootPath/swagger-ui/proxy/**")
            .addResourceLocations("classpath:/static/proxy/")
        registry.addResourceHandler("$uiRootPath/swagger-ui/**")
            .addResourceLocations("classpath:/static/")

        SwaggerWebMvcConfigurer(swaggerUiConfigParameters, swaggerIndexTransformer, actuatorProvider, swaggerResourceResolver).addResourceHandlers(registry)
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(ByteArrayHttpMessageConverter())
        converters.add(StringHttpMessageConverter())
        converters.add(ResourceHttpMessageConverter())
        converters.add(ResourceRegionHttpMessageConverter())
        converters.add(MappingJackson2HttpMessageConverter(Jackson.getMapper()))

        super.configureMessageConverters(converters)
    }

    @Bean
    override fun mvcConversionService(): FormattingConversionService {
        val conversionService = super.mvcConversionService()
        val dateTimeRegistrar = DateTimeFormatterRegistrar()
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ISO_DATE)
        dateTimeRegistrar.setTimeFormatter(DateTimeFormatter.ISO_TIME)
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME)
        dateTimeRegistrar.registerFormatters(conversionService)
        return conversionService
    }
}
