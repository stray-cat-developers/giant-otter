package io.mustelidae.grantotter.domain.spec

import io.mustelidae.grantotter.domain.client.SpecClientHandler
import io.mustelidae.grantotter.domain.spec.repository.SwaggerSpecRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI

@Service
class SwaggerSpecManager
@Autowired constructor(
    private val swaggerSpecRepository: SwaggerSpecRepository,
    private val swaggerSpecFinder: SwaggerSpecFinder,
    private val specClientHandler: SpecClientHandler,
) {

    fun add(swaggerSpec: SwaggerSpec): ObjectId {
        validate(swaggerSpec)

        swaggerSpecRepository.save(swaggerSpec)
        return swaggerSpec.getId()
    }

    fun remove(id: ObjectId) {
        val spec = swaggerSpecFinder.findOne(id)
        swaggerSpecRepository.delete(spec)
    }

    private fun validate(swaggerSpec: SwaggerSpec) {
        val client = specClientHandler.client(swaggerSpec.type)

        if (client.hasSpec(URI(swaggerSpec.url)).not()) {
            throw IllegalArgumentException("invalid url(${swaggerSpec.url}).")
        }
    }
}
