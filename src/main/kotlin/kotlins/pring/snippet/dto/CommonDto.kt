package kotlins.pring.snippet.dto

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

data class UpdateDto<T>(
    val id: Long,
    val data: T
)