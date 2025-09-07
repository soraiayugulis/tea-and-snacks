package sysout.openups.product.dto

import org.eclipse.microprofile.openapi.annotations.media.Schema
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.TeaCategory
import java.util.*

@Schema(name = "Tea", description = "Tea representation")
data class TeaDTO(
    @field:Schema(description = "Unique identifier", required = false, example = "123e4567-e89b-12d3-a456-426614174000")
    var id: UUID? = null,

    @field:Schema(description = "Tea name", required = true, example = "Earl Grey")
    var name: String = "",

    @field:Schema(description = "Country of origin", required = true, example = "India")
    var origin: String = "",

    @field:Schema(description = "Tea description", required = true, example = "A black tea flavored with bergamot")
    var description: String = "",

    @field:Schema(description = "Tea category", required = true, example = "BLACK", enumeration = ["BLACK", "GREEN", "HERBAL", "OOLONG", "WHITE", "FLORAL", "OTHER"])
    var category: TeaCategory = TeaCategory.OTHER,

    @field:Schema(description = "Level of caffeine", required = true, example = "MEDIUM", enumeration = ["NONE", "LOW", "MEDIUM", "HIGH"])
    var caffeineLevel: CaffeineLevel = CaffeineLevel.MEDIUM
)
