package kotlins.pring.snippet.entity

import jakarta.persistence.*
import kotlins.pring.snippet.config.converter.BooleanYNConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime

@Entity
@Table(name = "fruit_order")
data class FruitOrder (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Long? = null,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "fruit_id", nullable = false)
    var fruitId: Long,

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