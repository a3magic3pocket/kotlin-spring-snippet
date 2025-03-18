package kotlins.pring.snippet.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceException
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import kotlins.pring.snippet.config.exception.DuplicateFieldException
import kotlins.pring.snippet.config.exception.ServiceException
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class DuplicateCheckService(
    private val entityManager: EntityManager
) {
    companion object {
        const val ID_ENTITY_FIELD_NAME = "id"
        const val IS_DELETED_ENTITY_FIELD_NAME = "isDeleted"
    }

    // 중복 체크 메서드
    fun checkForDuplicate(
        entityClass: KClass<*>,
        fieldName: String,
        fieldValue: Any,
        idValue: Long? = null, // 업데이트 시 id 제공
        useSoftDelete: Boolean = true
    ) {
        try {
            val count = executeDuplicateCheckQuery(entityClass, fieldName, fieldValue, idValue, useSoftDelete)
            if (count > 0) {
                throw DuplicateFieldException(
                    field = fieldName,
                    value = fieldValue.toString(),
                )
            }
        } catch (e: DuplicateFieldException) {
            throw e
        } catch (e: PersistenceException) {
            throw ServiceException("duplicate check error - database access failure", e)
        } catch (e: Exception) {
            throw ServiceException("duplicate check error - unexpected error occurred", e)
        }
    }

    private fun executeDuplicateCheckQuery(
        entityClass: KClass<*>,
        fieldName: String,
        fieldValue: Any,
        idValue: Long?,
        useSoftDelete: Boolean
    ): Long {
        val criteriaBuilder: CriteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery: CriteriaQuery<Long> = criteriaBuilder.createQuery(Long::class.java)
        val root: Root<*> = criteriaQuery.from(entityClass.java)

        val predicates = ArrayList<Predicate>()

        // 필드 값 조건 설정 (e.$fieldName = :value)
        predicates.add(criteriaBuilder.equal(root.get<Any>(fieldName), fieldValue))

        // ID 조건 설정 (e.id != idValue)
        idValue?.let {
            predicates.add(criteriaBuilder.notEqual(root.get<Any>(ID_ENTITY_FIELD_NAME), it))
        }

        // 활성화된 데이터 조건 설정 (optional)
        if (useSoftDelete) {
            predicates.add(criteriaBuilder.equal(root.get<String>(IS_DELETED_ENTITY_FIELD_NAME), false))
        }

        // 쿼리 실행
        criteriaQuery.select(criteriaBuilder.count(root))
            .where(criteriaBuilder.and(*predicates.toTypedArray()))

        return entityManager.createQuery(criteriaQuery).singleResult
    }
}