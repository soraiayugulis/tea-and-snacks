package sysout.openups.controller.dto

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.*

@Schema(name = "Sauce", description = "Sauce representation")
data class SauceDTO(
    @field:Schema(description = "Unique identifier", required = false, example = "123e4567-e89b-12d3-a456-426614174000")
    var id: UUID? = null,

    @field:Schema(description = "Sauce name", required = true, example = "Mayonnaise")
    var name: String = "",

    @field:Schema(description = "Sauce flavor", required = true, example = "Creamy")
    var flavour: String = ""
)
