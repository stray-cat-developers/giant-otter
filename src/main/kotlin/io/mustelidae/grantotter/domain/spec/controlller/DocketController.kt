package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.domain.crawler.SwaggerDocCacheStore
import io.swagger.v3.oas.annotations.Operation
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/swagger/specifications")
class DocketController {

    /**
     * Docket 조회
     */
    @Operation(hidden = true)
    @GetMapping("{id}/docket", produces = ["text/plain;charset=UTF-8"])
    fun findOne(@PathVariable id: String): String? {
        return SwaggerDocCacheStore.findOne(ObjectId(id))?.second
    }
}
