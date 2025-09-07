package sysout.openups.product.dto

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.*

@Schema(name = "Snack", description = "Snack representation")
data class SnackDTO(
    @field:Schema(description = "Unique identifier", required = false, example = "123e4567-e89b-12d3-a456-426614174000")
    var id: UUID? = null,

    @field:Schema(description = "Snack name", required = true, example = "Cheese Sandwich")
    var name: String = "",

    @field:Schema(description = "Snack description", required = true, example = "A delicious sandwich with fresh bread and cheese")
    var description: String = "",

    @field:Schema(description = "Flavor profile", required = true, example = "Savory")
    var flavor: String = "",

    @field:Schema(description = "Whether the snack is vegan", required = true, example = "false")
    var vegan: Boolean = false,

    @field:Schema(description = "List of sauce IDs that go with this snack", required = false)
    var sides: List<UUID> = emptyList()
)
