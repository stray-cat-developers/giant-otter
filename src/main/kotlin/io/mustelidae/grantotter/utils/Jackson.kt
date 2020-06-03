package io.mustelidae.grantotter.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

object Jackson {
    private val mapper = Jackson2ObjectMapperBuilder.json()
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(JavaTimeModule())
        .serializers(CustomDateSerializer())
        .build<ObjectMapper>()
        .registerKotlinModule()
        .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)

    private val ymlMapper = ObjectMapper(YAMLFactory())

    fun getMapper(): ObjectMapper = mapper
    fun getYmlMapper(): ObjectMapper = ymlMapper
}

private class CustomDateSerializer : StdSerializer<Date>(Date::class.java) {
    override fun serialize(value: Date?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (value != null && gen != null) {
            gen.writeString(
                LocalDateTime.ofInstant(
                    value.toInstant(),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }
}

class KeepAsJsonDeserializer : JsonDeserializer<String>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): String {
        val tree: TreeNode = jp.codec.readTree(jp)
        return tree.toString()
    }
}

internal fun <T> T.toJson(): String = Jackson.getMapper().writeValueAsString(this)
internal inline fun <reified T> String.fromJson(): T = Jackson.getMapper().readValue(this)
