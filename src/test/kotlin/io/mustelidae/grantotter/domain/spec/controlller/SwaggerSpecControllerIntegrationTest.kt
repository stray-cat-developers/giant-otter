package io.mustelidae.grantotter.domain.spec.controlller

import io.kotlintest.matchers.asClue
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mustelidae.grantotter.config.IntegrationSupport
import io.mustelidae.grantotter.domain.ResourceFixture
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class SwaggerSpecControllerIntegrationTest : IntegrationSupport() {

    @Autowired
    private lateinit var swaggerSpecController: SwaggerSpecController

    @Test
    fun addNFindAll() {
        // Given
        val request = ResourceFixture.aSwaggerSpecRequest(port)

        // When
        val id = swaggerSpecController.add(request).content

        // Then
        id shouldNotBe null

        val spec = swaggerSpecController.findOne(id!!).content

        spec shouldNotBe null
        spec!!.asClue {
            it.id shouldBe id
            it.name shouldBe request.name
            it.type shouldBe request.type
            it.description shouldBe request.description
            it.url shouldBe request.url
        }

        val specs = swaggerSpecController.findAll().getContent()

        specs.size shouldBeGreaterThanOrEqual 1
    }
}
