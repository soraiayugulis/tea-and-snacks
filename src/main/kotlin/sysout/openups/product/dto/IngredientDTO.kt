package sysout.openups.product.dto

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.*

@Schema(name = "Ingredient", description = "Ingredient representation")
data class IngredientDTO(
    @field:Schema(description = "Unique identifier", required = false, example = "123e4567-e89b-12d3-a456-426614174000")
    var id: UUID? = null,

    @field:Schema(description = "Ingredient name", required = true, example = "Sugar")
    var name: String = "",

    @field:Schema(description = "Quantity", required = true, example = "10.0")
    var quantity: Double = 0.0,

    @field:Schema(description = "Unit of measure", required = true, example = "GRAMS", enumeration = ["GRAMS", "ML", "SPOON"])
    var unitOfMeasure: String = "GRAMS"
)

