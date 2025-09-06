package sysout.openups.controller.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tea")
open class Tea(
    @Id
    @GeneratedValue
    var id: UUID? = null,
    var name: String = "",
    var origin: String = "",
    var description: String = "",

    @Enumerated(EnumType.STRING)
    var category: TeaCategory = TeaCategory.OTHER,

    @Enumerated(EnumType.STRING)
    var caffeineLevel: CaffeineLevel = CaffeineLevel.MEDIUM
)
