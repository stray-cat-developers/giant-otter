package io.mustelidae.grantotter.domain.spec

import com.github.kittinunf.fuel.Fuel
import io.mustelidae.grantotter.domain.spec.repository.SwaggerSpecRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SwaggerSpecManager
@Autowired constructor(
    private val swaggerSpecRepository: SwaggerSpecRepository,
    private val swaggerSpecFinder: SwaggerSpecFinder
) {

    fun add(swaggerSpec: SwaggerSpec): ObjectId {
        validate(swaggerSpec)

        swaggerSpecRepository.save(swaggerSpec)
        return swaggerSpec.id
    }

    fun remove(id: ObjectId) {
        val spec = swaggerSpecFinder.findOne(id)
        swaggerSpecRepository.delete(spec)
    }

    private fun validate(swaggerSpec: SwaggerSpec) {
        val headers = swaggerSpec.headers ?: mutableMapOf()
        headers.toMutableMap()["Content-Type"] = "application/json"

        val result = Fuel.get(swaggerSpec.url)
            .header(headers)
            .responseString()
            .second
            .statusCode

        if (HttpStatus.valueOf(result).is2xxSuccessful.not())
            throw IllegalArgumentException("invalid url. cause by http status is $result")
    }
}
