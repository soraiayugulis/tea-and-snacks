package sysout.openups.controller.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
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
    var category: String = "",
    var caffeineLevel: String = ""
)
