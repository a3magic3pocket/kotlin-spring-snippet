package kotlins.pring.snippet.config.exception

class NotFoundException(
    message: String = "not found",
    val code: String = "404",
) : RuntimeException(message)