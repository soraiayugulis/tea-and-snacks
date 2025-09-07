package sysout.openups.product.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "snack")
open class Snack {
    @Id
    @GeneratedValue
    var id: UUID? = null
    var name: String = ""
    var description: String = ""
    var flavor: String = ""
    var vegan: Boolean = false
    @ManyToMany(fetch = FetchType.LAZY)
    var sides: MutableList<Sauce> = mutableListOf()
}
