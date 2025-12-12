package sysout.openups.product.entity

import jakarta.persistence.Embeddable
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import java.util.UUID

@Embeddable
data class Ingredient(
    var id: UUID? = null,
    var name: String = "",
    var quantity: Double = 0.0,
    @Enumerated(EnumType.STRING)
    var unitOfMeasure: UnitOfMeasure = UnitOfMeasure.GRAMS
)
