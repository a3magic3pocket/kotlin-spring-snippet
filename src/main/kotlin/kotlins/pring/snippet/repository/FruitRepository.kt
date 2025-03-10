package kotlins.pring.snippet.repository

import kotlins.pring.snippet.entity.Fruit
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FruitRepository : JpaRepository<Fruit, Long> {
    fun findByName(name: String): List<Fruit>
    fun findByNameContaining(name: String): List<Fruit>
    fun findByNameStartingWith(name: String): List<Fruit>
    fun findByNameEndingWith(name: String): List<Fruit>
    fun findByOriginIn(origins: List<String>): List<Fruit>
    fun findAllByOrderByNameAscOriginDesc(): List<Fruit>
    fun existsByName(name: String): Boolean
    fun countByName(name: String): Long
    fun findByOrigin(origin: String, pageable: Pageable): Page<Fruit>
    fun findByIdIn(ids: List<Long>): List<Fruit>
}
