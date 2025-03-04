package kotlins.pring.snippet.repository

import kotlins.pring.snippet.entity.Fruit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FruitRepository : JpaRepository<Fruit, Long> {
    fun findByName(name: String): List<Fruit>
}
