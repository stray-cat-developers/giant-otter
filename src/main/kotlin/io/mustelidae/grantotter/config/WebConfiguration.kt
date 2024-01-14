package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.utils.Jackson
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
import java.time.format.DateTimeFormatter

@Configuration
@ControllerAdvice
class WebConfiguration : DelegatingWebMvcConfiguration() {

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
