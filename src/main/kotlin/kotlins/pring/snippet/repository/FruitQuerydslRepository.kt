package kotlins.pring.snippet.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kotlins.pring.snippet.dto.FruitOrderDto
import kotlins.pring.snippet.dto.FruitOrderLatestAndPreviousDiff
import kotlins.pring.snippet.dto.QFruitOrderDto
import kotlins.pring.snippet.dto.QFruitOrderLatestAndPreviousDiff
import kotlins.pring.snippet.entity.QFruit
import kotlins.pring.snippet.entity.QFruitOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class FruitQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun getFruitsWithQuantity(): MutableList<FruitOrderDto> {
        val fruit = QFruit.fruit
        val fruitOrder = QFruitOrder.fruitOrder

        return jpaQueryFactory.select(
            QFruitOrderDto(
                fruit,
                fruitOrder
            )
        ).from(fruit)
            .innerJoin(fruitOrder).on(fruit.id.eq(fruitOrder.fruitId))
            .where(
                fruit.isDeleted.eq(false),
                fruitOrder.isDeleted.eq(false)
            ).fetch()
    }

    fun getFruitsWithQuantityPageable(pageable: Pageable): Page<FruitOrderDto> {
        val fruit = QFruit.fruit
        val fruitOrder = QFruitOrder.fruitOrder

        val builder = BooleanBuilder()
        builder.and(fruit.isDeleted.eq(false))
        builder.and(fruitOrder.isDeleted.eq(false))

        val data = jpaQueryFactory.select(
            QFruitOrderDto(
                fruit,
                fruitOrder
            )
        ).from(fruit)
            .innerJoin(fruitOrder).on(fruit.id.eq(fruitOrder.fruitId))
            .where(builder)
            .offset((pageable.pageNumber * pageable.pageSize).toLong())
            .limit(pageable.pageSize.toLong())
            .fetch()

        val count = jpaQueryFactory.select(
            fruit.count()
        ).from(fruit)
            .innerJoin(fruitOrder).on(fruit.id.eq(fruitOrder.fruitId))
            .where(builder)
            .fetchOne()

        return PageableExecutionUtils.getPage(data, pageable) { count ?: 0L }
    }

    fun getLatestAndPreviousOrderDiff(): MutableList<FruitOrderLatestAndPreviousDiff> {
        val fruitOrder1 = QFruitOrder("fo1")
        val fruitOrder2 = QFruitOrder("fo2")

        return jpaQueryFactory.select(
            QFruitOrderLatestAndPreviousDiff(
                fruitOrder1.fruitId,
                fruitOrder1.id,
                fruitOrder2.id,
                fruitOrder1.quantity,
                fruitOrder2.quantity,
                fruitOrder1.quantity.subtract(fruitOrder2.quantity)// 수량 차이 계산
            )
        )
            .from(fruitOrder1)
            .innerJoin(fruitOrder2)
            .on(
                fruitOrder1.fruitId.eq(fruitOrder2.fruitId)
                    .and(fruitOrder1.updatedAt.gt(fruitOrder2.updatedAt))
            )
            .where(
                fruitOrder1.isDeleted.eq(false),
                fruitOrder2.isDeleted.eq(false),
                fruitOrder1.id.ne(fruitOrder2.id)
            )
            .orderBy(
                fruitOrder1.fruitId.asc(), fruitOrder1.updatedAt.desc()
            )
            .fetch()
    }

}
