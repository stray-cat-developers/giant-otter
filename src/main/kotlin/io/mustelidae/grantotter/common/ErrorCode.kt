package io.mustelidae.grantotter.common

enum class ErrorCode(val desc: String) {
    // Human error
    H000("Data Not Found"),
    H001("Invalid Input"),

    // System error
    S000("Internal Server Error"),

    // Communication error
    C000("Communication error"),
}
