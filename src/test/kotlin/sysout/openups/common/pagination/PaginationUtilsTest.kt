package sysout.openups.common.pagination

import jakarta.ws.rs.BadRequestException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sysout.openups.common.Constants

class PaginationUtilsTest {
    @Test
    fun `validateAndGetPageSize should return default size when null is provided`() {
        val result = PaginationUtils.validateAndGetPageSize(null)
        Assertions.assertEquals(Constants.Pagination.Properties.DEFAULT_PAGE_SIZE, result)
    }

    @Test
    fun `validateAndGetPageSize should return provided size when valid`() {
        val result = PaginationUtils.validateAndGetPageSize(3)
        Assertions.assertEquals(3, result)
    }

    @Test
    fun `validateAndGetPageSize should limit to max size when exceeding`() {
        val result = PaginationUtils.validateAndGetPageSize(20)
        Assertions.assertEquals(Constants.Pagination.Properties.MAX_PAGE_SIZE, result)
    }

    @Test
    fun `validateAndGetPageSize should throw exception for zero`() {
        val exception = assertThrows<BadRequestException> {
            PaginationUtils.validateAndGetPageSize(0)
        }
        Assertions.assertEquals(Constants.Pagination.Error.PAGE_SIZE_INVALID, exception.message)
    }

    @Test
    fun `validateAndGetPageSize should throw exception for negative value`() {
        val exception = assertThrows<BadRequestException> {
            PaginationUtils.validateAndGetPageSize(-1)
        }
        Assertions.assertEquals(Constants.Pagination.Error.PAGE_SIZE_INVALID, exception.message)
    }

    @Test
    fun `validateAndGetPageSize should accept MAX_PAGE_SIZE value`() {
        val result = PaginationUtils.validateAndGetPageSize(Constants.Pagination.Properties.MAX_PAGE_SIZE)
        Assertions.assertEquals(Constants.Pagination.Properties.MAX_PAGE_SIZE, result)
    }

    @Test
    fun `validateAndGetPageNumber should return default page when null is provided`() {
        val result = PaginationUtils.validateAndGetPageNumber(null)
        Assertions.assertEquals(Constants.Pagination.Properties.DEFAULT_PAGE_NUMBER, result)
    }

    @Test
    fun `validateAndGetPageNumber should return provided page when valid`() {
        val result = PaginationUtils.validateAndGetPageNumber(2)
        Assertions.assertEquals(2, result)
    }

    @Test
    fun `validateAndGetPageNumber should throw exception for negative value`() {
        val exception = assertThrows<BadRequestException> {
            PaginationUtils.validateAndGetPageNumber(-1)
        }
        Assertions.assertEquals(Constants.Pagination.Error.PAGE_NUMBER_INVALID, exception.message)
    }

    @Test
    fun `validateAndGetPageNumber should accept zero as valid page number`() {
        val result = PaginationUtils.validateAndGetPageNumber(0)
        Assertions.assertEquals(0, result)
    }

    @Test
    fun `createPaginatedResponse should calculate total pages correctly`() {
        val response = PaginationUtils.createPaginatedResponse(
            data = listOf("item1", "item2"),
            totalElements = 5L,
            pageSize = 2,
            currentPage = 0
        )

        Assertions.assertEquals(3, response.totalPages) // ceil(5/2) = 3 pages
        Assertions.assertEquals(5L, response.totalElements)
        Assertions.assertEquals(2, response.pageSize)
        Assertions.assertEquals(0, response.currentPage)
        Assertions.assertEquals(2, response.data.size)
    }

    @Test
    fun `createPaginatedResponse should handle empty data`() {
        val response = PaginationUtils.createPaginatedResponse(
            data = emptyList<String>(),
            totalElements = 0L,
            pageSize = 10,
            currentPage = 0
        )

        Assertions.assertEquals(0, response.totalPages)
        Assertions.assertEquals(0L, response.totalElements)
        Assertions.assertEquals(10, response.pageSize)
        Assertions.assertEquals(0, response.currentPage)
        Assertions.assertTrue(response.data.isEmpty())
    }

    @Test
    fun `createPaginatedResponse should handle last page with fewer items`() {
        val response = PaginationUtils.createPaginatedResponse(
            data = listOf("last item"),
            totalElements = 5L,
            pageSize = 2,
            currentPage = 2
        )

        Assertions.assertEquals(3, response.totalPages)
        Assertions.assertEquals(5L, response.totalElements)
        Assertions.assertEquals(2, response.pageSize)
        Assertions.assertEquals(2, response.currentPage)
        Assertions.assertEquals(1, response.data.size)
    }

    @Test
    fun `createPaginatedResponse should handle when totalElements is less than pageSize`() {
        val response = PaginationUtils.createPaginatedResponse(
            data = listOf("single item"),
            totalElements = 1L,
            pageSize = 10,
            currentPage = 0
        )

        Assertions.assertEquals(1, response.totalPages)
        Assertions.assertEquals(1L, response.totalElements)
        Assertions.assertEquals(10, response.pageSize)
        Assertions.assertEquals(0, response.currentPage)
        Assertions.assertEquals(1, response.data.size)
    }

    @Test
    fun `createPaginatedResponse should handle when totalElements is exact multiple of pageSize`() {
        val response = PaginationUtils.createPaginatedResponse(
            data = listOf("item1", "item2"),
            totalElements = 4L,
            pageSize = 2,
            currentPage = 1
        )

        Assertions.assertEquals(2, response.totalPages) // exactly 2 pages (4/2)
        Assertions.assertEquals(4L, response.totalElements)
        Assertions.assertEquals(2, response.pageSize)
        Assertions.assertEquals(1, response.currentPage)
        Assertions.assertEquals(2, response.data.size)
    }
}