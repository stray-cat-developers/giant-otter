package io.mustelidae.grantotter.domain.spec

import io.mustelidae.grantotter.domain.spec.repository.SwaggerSpecRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SwaggerSpecManager
@Autowired constructor(
    private val swaggerSpecRepository: SwaggerSpecRepository,
    private val swaggerSpecFinder: SwaggerSpecFinder
) {

    fun add(swaggerSpec: SwaggerSpec): ObjectId {
        swaggerSpecRepository.save(swaggerSpec)
        return swaggerSpec.id
    }

    fun remove(id: ObjectId) {
        val spec = swaggerSpecFinder.findOne(id)
        swaggerSpecRepository.delete(spec)
    }
}
