package kotlins.pring.snippet.config.exception

class DuplicateFieldException(
    val field: String,
    val value: String,
    message: String = "The value '$value' for field '$field' is already in use.", // 기본 오류 메시지
    val code: String = "400",
) : RuntimeException(message)
