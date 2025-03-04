package kotlins.pring.snippet.entity

import jakarta.persistence.*
import kotlins.pring.snippet.config.converter.BooleanYNConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime

/**
 * 과일
 */
@Entity
@Table(name = "fruit")
data class Fruit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "origin", nullable = false)
    var origin: String,

    @Column(name = "size", nullable = true)
    var size: Int = 0,

    @Column(name = "freshness", nullable = false)
    @Enumerated(EnumType.STRING)
    var freshness: FruitFreshness = FruitFreshness.FRESH,

    @Column(name = "is_deleted", nullable = false)
    @Convert(converter = BooleanYNConverter::class)
    var isDeleted: Boolean = false,

    @Column(name = "created_at", nullable = true, insertable = false, updatable = false)
    @CreationTimestamp
    var createdAt: ZonedDateTime? = null,

    @Column(name = "updated_at", nullable = true, insertable = true, updatable = true)
    @UpdateTimestamp
    var updatedAt: ZonedDateTime? = null

)