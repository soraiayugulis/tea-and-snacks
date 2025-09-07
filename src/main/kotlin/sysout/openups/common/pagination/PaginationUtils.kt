package sysout.openups.common.pagination

import jakarta.ws.rs.BadRequestException
import sysout.openups.common.Constants.Pagination.Error.PAGE_NUMBER_INVALID
import sysout.openups.common.Constants.Pagination.Error.PAGE_SIZE_INVALID
import sysout.openups.common.Constants.Pagination.Properties.DEFAULT_PAGE_NUMBER
import sysout.openups.common.Constants.Pagination.Properties.DEFAULT_PAGE_SIZE
import sysout.openups.common.Constants.Pagination.Properties.MAX_PAGE_SIZE

object PaginationUtils {
    fun validateAndGetPageSize(size: Int?): Int {
        val pageSize = size ?: DEFAULT_PAGE_SIZE
        if (pageSize <= 0) {
            throw BadRequestException(PAGE_SIZE_INVALID)
        }
        return pageSize.coerceAtMost(MAX_PAGE_SIZE)
    }

    fun validateAndGetPageNumber(page: Int?): Int {
        val pageNumber = page ?: DEFAULT_PAGE_NUMBER
        if (pageNumber < 0) {
            throw BadRequestException(PAGE_NUMBER_INVALID)
        }
        return pageNumber
    }

    fun <T> createPaginatedResponse(
        data: List<T>,
        totalElements: Long,
        pageSize: Int,
        currentPage: Int
    ): PaginatedResponse<T> {
        val totalPages = if (totalElements == 0L) 0 else ((totalElements - 1) / pageSize + 1).toInt()
        return PaginatedResponse(
            data = data,
            totalElements = totalElements,
            totalPages = totalPages,
            currentPage = currentPage,
            pageSize = pageSize
        )
    }
}
