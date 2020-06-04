package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.domain.crawler.SwaggerDocCacheStore
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

/**
 * Spec에 저장
 */
@ApiIgnore
@RestController
@RequestMapping("/swagger/specifications")
class DocketController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("{id}/docket", produces = ["text/plain;charset=UTF-8"])
    fun findOne(@PathVariable id: String): String? {
        return SwaggerDocCacheStore.findOne(ObjectId(id))?.second
    }
}
