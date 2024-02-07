package io.mustelidae.grantotter.domain.spec

import com.fasterxml.jackson.databind.JsonNode
import io.mustelidae.grantotter.utils.Jackson
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.converter.SwaggerConverter
import io.swagger.v3.parser.core.models.ParseOptions
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.providers.ObjectMapperProvider
import org.springframework.data.util.Version

class SpecConvertor(
    private val type: SwaggerSpec.Type,
    private val sourceJsonSpec: String,
) {

    fun convertToV3(springDocConfigProperties: SpringDocConfigProperties): String {
        val specVersion = getVersion() ?: throw IllegalArgumentException("Version information does not exist.")
        val version = Version.parse(specVersion)

        // open api 2.X
        if (version.isLessThan(Version(3, 0, 0))) {
            val converter = SwaggerConverter()
            val parseOptions = ParseOptions()

            val openAPI = converter.readContents(sourceJsonSpec, null, parseOptions).openAPI

            val objectMapper = when (type) {
                SwaggerSpec.Type.JSON -> ObjectMapperProvider(springDocConfigProperties).jsonMapper()
                SwaggerSpec.Type.YAML -> ObjectMapperProvider(springDocConfigProperties).yamlMapper()
            }

            val result = objectMapper
                .writerWithDefaultPrettyPrinter()
                .forType(OpenAPI::class.java)
                .writeValueAsString(openAPI)

            return result
        }

        return sourceJsonSpec
    }

    fun getVersion(): String? {
        val versionNode: JsonNode? = when (type) {
            SwaggerSpec.Type.JSON -> {
                try {
                    val treeNode = Jackson.getMapper().readTree(sourceJsonSpec)
                    treeNode.get("swagger") ?: treeNode.get("openapi")
                } catch (e: Exception) {
                    throw IllegalStateException("The format of the Open API Spec is not Json Type.")
                }
            }
            SwaggerSpec.Type.YAML -> {
                try {
                    val treeNode = Jackson.getYmlMapper().readTree(sourceJsonSpec)
                    treeNode.get("swagger") ?: treeNode.get("openapi")
                } catch (e: Exception) {
                    throw IllegalStateException("The format of the Open API Spec is not YAML Type.")
                }
            }
        }

        return versionNode?.textValue()
    }

    fun isV2(): Boolean {
        val specVersion = getVersion() ?: throw IllegalArgumentException("Version information does not exist.")
        val version = Version.parse(specVersion)

        return version.isLessThan(Version(3, 0, 0))
    }
}
