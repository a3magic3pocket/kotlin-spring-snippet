package kotlins.pring.snippet.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryProjection
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlins.pring.snippet.entity.Fruit
import kotlins.pring.snippet.entity.FruitOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FruitReqDto(
    @JsonProperty("fruit_name")
    val name: String,

    @JsonProperty("fruit_origin")
    val origin: String,
)

data class FruitReqFormDataDto(
    val name: String,
    val origin: String,
)

data class FruitFileDto(
    val name: String,
    val fruitName: String,
    val fruitOrigin: String,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FruitSizeDto(
    @field:NotNull(message = "null 허용 안 함")
    @field:NotEmpty(message = "\"\" 허용 안 함")
    @JsonProperty("fruit_name")
    val name: String,

    @JsonProperty("fruit_origin")
    val origin: String,

    @field:Min(value = 1, message = "1 이상이어야 함")
    @field:Max(value = 10, message = "10 이하이어야 함")
    val size: Int,
)

data class FruitResDto(
    val id: Long,
    val name: String,
    val origin: String,
)

data class FruitOrderDto @QueryProjection constructor(
    val fruit: Fruit,
    val fruitOrder: FruitOrder,
)

data class FruitOrderLatestAndPreviousDiff @QueryProjection constructor(
    val fruitId: Long,
    val baseOrderId: Long,
    val previousOrderId: Long,
    val baseOrderQuantity: Int,
    val previousOrderQuantity: Int,
    val diff: Int,
)
