package sysout.openups.controller.dto

import java.util.*

data class SnackDTO(
    var id: UUID? = null,
    var name: String = "",
    var description: String = "",
    var flavor: String = "",
    var vegan: Boolean = false,
    var sides: List<UUID> = emptyList()
)
