package io.mustelidae.grantotter.domain.spec.repository

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import org.bson.types.ObjectId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SwaggerSpecRepository : JpaRepository<SwaggerSpec, ObjectId> {
    fun findBySpecId(specId: String): SwaggerSpec?
}
