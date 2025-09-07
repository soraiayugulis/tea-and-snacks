package sysout.openups.product.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "sauce")
open class Sauce(
    @Id
    @GeneratedValue
    var id: UUID? = null,
    var name: String = "",
    var flavour: String = ""
)
