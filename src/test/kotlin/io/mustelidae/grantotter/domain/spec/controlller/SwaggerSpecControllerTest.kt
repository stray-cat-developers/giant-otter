package io.mustelidae.grantotter.domain.spec.controlller

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.grantotter.config.FlowTestSupport
import io.mustelidae.grantotter.domain.ResourceFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties

class SwaggerSpecControllerTest: FlowTestSupport() {

    @Autowired
    private lateinit var serverProperties: ServerProperties

    @Test
    fun addNFindAll() {
        val port = serverProperties.port
        val swaggerSpecControllerFlow = SwaggerSpecControllerFlow(mockMvc)

        val request = ResourceFixture.aSwaggerSpecRequest(port)
        val id = swaggerSpecControllerFlow.add(request)

        val spec = swaggerSpecControllerFlow.findOne(id)

        spec shouldNotBe null
        spec.asClue {
            it.id shouldBe id
            it.name shouldBe "[${request.category}] ${request.name}"
            it.type shouldBe request.type
            it.description shouldBe request.description
            it.url shouldBe request.url
        }
    }

    @Test
    fun addFail() {
        val swaggerSpecControllerFlow = SwaggerSpecControllerFlow(mockMvc)

        val request = ResourceFixture.aSwaggerSpecInvalidRequest()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            swaggerSpecControllerFlow.add(request)
        }
    }
}