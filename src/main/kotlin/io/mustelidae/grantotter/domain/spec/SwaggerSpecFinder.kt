package io.mustelidae.grantotter.domain.spec

import io.mustelidae.grantotter.config.DataNotFoundException
import io.mustelidae.grantotter.domain.spec.repository.SwaggerSpecRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SwaggerSpecFinder
@Autowired constructor(
    private val swaggerSpecRepository: SwaggerSpecRepository,
) {
    fun findOne(id: ObjectId): SwaggerSpec {
        val spec = swaggerSpecRepository.findById(id)
        if (spec.isPresent.not()) {
            throw DataNotFoundException("specification not found.")
        }

        return spec.get()
    }

    fun findAll(): List<SwaggerSpec> {
        return swaggerSpecRepository.findAll()
    }
}
