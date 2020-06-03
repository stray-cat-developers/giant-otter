package io.mustelidae.grantotter.common

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.annotations.ApiModel

open class Reply<T>() {
    @get:JsonUnwrapped
    var content: T? = null

    constructor(content: T) : this() {
        this.content = content
    }

    override fun toString(): String {
        return String.format("Resource { content: %s, %s }", content, super.toString())
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) {
            return true
        }

        if (other == null || other.javaClass != javaClass) {
            return false
        }

        val that = other as Reply<*>?

        val contentEqual = if (this.content == null) that!!.content == null else this.content == that!!.content
        return if (contentEqual) super.equals(other) else contentEqual
    }

    override fun hashCode(): Int {

        var result = super.hashCode()
        result += if (content == null) 0 else 17 * content!!.hashCode()
        return result
    }
}
