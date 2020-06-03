package io.mustelidae.grantotter.domain.spec.repository

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SwaggerSpecRepository : MongoRepository<SwaggerSpec, ObjectId>
