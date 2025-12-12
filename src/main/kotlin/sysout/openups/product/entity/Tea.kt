package sysout.openups.product.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "tea")
open class Tea(
    @Id
    @GeneratedValue
    var id: UUID? = null,
    var name: String = "",
    var origin: String = "",
    var description: String = "",

    @ElementCollection
    @CollectionTable(name = "tea_ingredient", joinColumns = [JoinColumn(name = "tea_id")])
    var ingredients: List<Ingredient> = emptyList(),

    @Enumerated(EnumType.STRING)
    var category: TeaCategory = TeaCategory.OTHER,

    @Enumerated(EnumType.STRING)
    var caffeineLevel: CaffeineLevel = CaffeineLevel.MEDIUM
)
