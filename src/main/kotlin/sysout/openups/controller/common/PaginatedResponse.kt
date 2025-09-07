package sysout.openups.controller.common

data class PaginatedResponse<T>(
    val data: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)
