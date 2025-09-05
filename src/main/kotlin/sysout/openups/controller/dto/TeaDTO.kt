package sysout.openups.controller.dto

import java.util.*

data class TeaDTO(
    var id: UUID? = null,
    var name: String = "",
    var origin: String = "",
    var description: String = "",
    var category: String = "",
    var caffeineLevel: String = ""
)
