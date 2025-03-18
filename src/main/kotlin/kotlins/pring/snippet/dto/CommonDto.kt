package kotlins.pring.snippet.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

data class DataPageableRes<T>(
    val totalCount: Long,
    val data: List<T>,
    val page: PageInfo
)

data class PageInfo(
    val totalPages: Int,
    val number: Int,
    val size: Int,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateReqDto<T>(
    val id: Long,
    val data: T
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorRes(
    var code: String,
    var timestamp: Date,
    var path: String,
    var message: String,
    var fieldErrors: List<FieldError> = listOf()
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FieldError(
    var field: String,
    var message: String
)