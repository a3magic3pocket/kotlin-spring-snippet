package kotlins.pring.snippet.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import kotlins.pring.snippet.config.extension.getColumnName
import kotlins.pring.snippet.config.extension.getTableName
import kotlins.pring.snippet.config.extension.safeGet
import kotlins.pring.snippet.dto.*
import kotlins.pring.snippet.entity.Fruit
import kotlins.pring.snippet.entity.FruitOrder
import kotlins.pring.snippet.entity.QFruit
import kotlins.pring.snippet.entity.QFruitOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class FruitQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
    private val entityManager: EntityManager,
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

    fun getFruitsOrderedByQuantity(quantity: Int): MutableList<Fruit> {
        val fruit = QFruit.fruit
        val fruitOrder = QFruitOrder.fruitOrder

        return jpaQueryFactory.select(
            fruit
        )
            .from(fruit)
            .where(
                fruit.id.`in`(
                    JPAExpressions.selectDistinct(
                        fruitOrder.fruitId
                    ).from(fruitOrder)
                        .where(
                            fruitOrder.quantity.eq(quantity),
                            fruitOrder.isDeleted.eq(false)
                        )
                ),
                fruit.isDeleted.eq(false)
            )
            .fetch()
    }

    fun getFruitsWithOrderSummary(): List<FruitOrderSummaryDto> {
        val cteT = "cte"
        val fruitOrderT = getTableName(FruitOrder::class)
        val foFruitIdC = getColumnName(FruitOrder::class, FruitOrder::fruitId.name)
        val foQuantityC = getColumnName(FruitOrder::class, FruitOrder::quantity.name)
        val foIdC = getColumnName(FruitOrder::class, FruitOrder::id.name)
        val foIsDeletedC = getColumnName(FruitOrder::class, FruitOrder::isDeleted.name)
        val cteQuantityAlias = "sum_quantities"
        val cteCountAlias = "count_orders"
        val fruitTAlias = "f"
        val fruitT = getTableName(Fruit::class)
        val fIdC = getColumnName(Fruit::class, Fruit::id.name)
        val fNameC = getColumnName(Fruit::class, Fruit::name.name)
        val fIsDeletedC = getColumnName(Fruit::class, Fruit::isDeleted.name)
        val foIsDeletedParam = "fo_is_deleted"
        val fIsDeletedParam = "f_is_deleted"

        val sql = """
                WITH `$cteT` AS (
                  SELECT `$foFruitIdC`, sum(`$foQuantityC`) AS `$cteQuantityAlias`, count(`$foIdC`) AS `$cteCountAlias`
                  FROM `$fruitOrderT`
                  WHERE `$foIsDeletedC` = :$foIsDeletedParam
                  GROUP BY `$foFruitIdC`
                )
                SELECT `$fruitTAlias`.`$fIdC`, `$fruitTAlias`.`$fNameC`, `$cteT`.`$cteQuantityAlias`, `$cteT`.`$cteCountAlias`  
                FROM `$fruitT` AS `$fruitTAlias`
                INNER JOIN `$cteT` ON `$fruitTAlias`.`$fIdC` = `$cteT`.`$foFruitIdC`
                WHERE `$fruitTAlias`.`$fIsDeletedC` = :$fIsDeletedParam
                ;
        """.trimIndent()

        // 쿼리 생성
        val query = entityManager.createNativeQuery(sql)

        // 파라미터 할당
        query.setParameter(foIsDeletedParam, 'N')
        query.setParameter(fIsDeletedParam, 'N')

        return query.resultList.mapNotNull { row ->
            FruitOrderSummaryDto(
                id = (row as? Array<*>)?.safeGet<Long>(0) ?: return@mapNotNull null,
                name = row.safeGet<String>(1) ?: return@mapNotNull null,
                sumQuantities = row.safeGet<Long>(2) ?: return@mapNotNull null,
                countOrders = row.safeGet<Long>(3) ?: return@mapNotNull null,
            )
        }
    }

    fun joinByFruitName(name: String): List<Any> {
        val fruitOrderT = getTableName(FruitOrder::class)
        val foFruitIdC = getColumnName(FruitOrder::class, FruitOrder::fruitId.name)
        val foIdC = getColumnName(FruitOrder::class, FruitOrder::id.name)
        val foIsDeletedC = getColumnName(FruitOrder::class, FruitOrder::isDeleted.name)
        val fruitOrderTAlias = "fo"
        val fruitTAlias = "refined_f"
        val fruitT = getTableName(Fruit::class)
        val fIdC = getColumnName(Fruit::class, Fruit::id.name)
        val fNameC = getColumnName(Fruit::class, Fruit::name.name)
        val fIsDeletedC = getColumnName(Fruit::class, Fruit::isDeleted.name)
        val foIsDeletedParam = "fo_is_deleted"
        val fIsDeletedParam = "f_is_deleted"
        val fNameParam = "f_name"

        val sql = """
            SELECT `$fruitOrderTAlias`.`$foIdC`, `$fruitOrderTAlias`.`$foFruitIdC` 
            FROM `$fruitOrderT` AS `$fruitOrderTAlias`
            INNER JOIN (
              SELECT `$fIdC`
              FROM `$fruitT`
              WHERE `$fNameC` = :$fNameParam AND `$fIsDeletedC` = :$fIsDeletedParam
            ) AS `$fruitTAlias`
            ON `$fruitOrderTAlias`.`$foFruitIdC` = `$fruitTAlias`.`$fIdC`
            WHERE `$fruitOrderTAlias`.`$foIsDeletedC` = :$foIsDeletedParam
            ;
        """.trimIndent()

        // 쿼리 생성
        val query = entityManager.createNativeQuery(sql)

        // 파라미터 할당
        query.setParameter(fNameParam, name)
        query.setParameter(foIsDeletedParam, 'N')
        query.setParameter(fIsDeletedParam, 'N')

        return query.resultList.mapNotNull { row ->
            FruitOrderSubqueryJoinDto(
                id = (row as? Array<*>)?.safeGet<Long>(0) ?: return@mapNotNull null,
                fruitId = row.safeGet<Long>(1) ?: return@mapNotNull null,
            )
        }
    }

}
