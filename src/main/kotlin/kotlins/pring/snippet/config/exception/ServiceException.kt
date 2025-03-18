package kotlins.pring.snippet.config.exception

class ServiceException(
    message: String = "server error occurred",
    cause: Throwable? = null,
    val code: String = "500",
) : RuntimeException(message, cause)
