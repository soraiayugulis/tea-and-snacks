package sysout.openups.product

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import sysout.openups.common.Constants.Http.Status.BAD_REQUEST
import sysout.openups.common.Constants.Http.Status.CREATED
import sysout.openups.common.Constants.Http.Status.NOT_FOUND
import sysout.openups.common.Constants.Http.Status.NO_CONTENT
import sysout.openups.common.Constants.Http.Status.OK
import sysout.openups.common.Constants.List.SAUCE_FILTERED
import sysout.openups.common.Constants.Message.Error.Entity.SAUCE_INVALID_DATA
import sysout.openups.common.Constants.Message.Error.Entity.SAUCE_NOT_FOUND
import sysout.openups.common.Constants.Message.Success.Entity.SAUCE_CREATED
import sysout.openups.common.Constants.Message.Success.Entity.SAUCE_DELETED
import sysout.openups.common.Constants.Message.Success.Entity.SAUCE_FOUND
import sysout.openups.common.Constants.Message.Success.Entity.SAUCE_UPDATED
import sysout.openups.common.Constants.Operation.SAUCE_ADD
import sysout.openups.common.Constants.Operation.SAUCE_DELETE
import sysout.openups.common.Constants.Operation.SAUCE_DELETE_ALL
import sysout.openups.common.Constants.Operation.SAUCE_FIND_BY_ID
import sysout.openups.common.Constants.Operation.SAUCE_UPDATE
import sysout.openups.common.Constants.Pagination.Params.PAGE_NUMBER_PARAM
import sysout.openups.common.Constants.Pagination.Params.PAGE_SIZE_PARAM
import sysout.openups.common.pagination.PaginatedResponse
import sysout.openups.product.dto.SauceDTO
import sysout.openups.product.service.SauceService
import java.util.*

@Path("/sauces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sauces", description = "Sauce operations")
class SauceResource @Inject constructor(
    private val sauceService: SauceService
) {
    @GET
    @Operation(
        summary = SAUCE_FILTERED,
        description = "Returns a paginated list of sauces that can be filtered by flavor"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_FILTERED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = PaginatedResponse::class)
                )]
            )
        ]
    )
    fun list(
        @Parameter(description = "Filter by flavor")
        @QueryParam("flavour") flavour: String?,

        @Parameter(description = "Page size (default: 5)")
        @QueryParam(PAGE_SIZE_PARAM) size: Int?,

        @Parameter(description = "Page number (default: 0)")
        @QueryParam(PAGE_NUMBER_PARAM) page: Int?,

        @Parameter(description = "Use pagination (default: true)")
        @QueryParam("paginated") paginated: Boolean?
    ): Response {
        return if (paginated ?: true) {
            val result = sauceService.filterSauces(flavour, page ?: 0, size ?: 5)
            Response.ok(result).build()
        } else {
            val result = sauceService.filterSauces(flavour)
            Response.ok(result).build()
        }
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = SAUCE_FIND_BY_ID,
        description = "Returns a specific sauce by its ID"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_FOUND,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SauceDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SAUCE_NOT_FOUND
            )
        ]
    )
    fun getById(
        @Parameter(description = "Sauce ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val sauce = sauceService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(sauce).build()
    }

    @POST
    @Operation(
        summary = SAUCE_ADD,
        description = "Creates a new sauce in the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = CREATED,
                description = SAUCE_CREATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SauceDTO::class)
                )]
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = SAUCE_INVALID_DATA
            )
        ]
    )
    fun add(
        @Parameter(description = "Sauce data to be added", required = true) dto: SauceDTO
    ): Response {
        val created = sauceService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = SAUCE_UPDATE,
        description = "Updates an existing sauce's data"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_UPDATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SauceDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SAUCE_NOT_FOUND
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = SAUCE_INVALID_DATA
            )
        ]
    )
    fun update(
        @Parameter(description = "Sauce ID", required = true) @PathParam("id") id: UUID,
        @Parameter(description = "New sauce data", required = true) dto: SauceDTO
    ): Response {
        val updated = sauceService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = SAUCE_DELETE,
        description = "Removes a sauce from the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = NO_CONTENT,
                description = SAUCE_DELETED
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SAUCE_NOT_FOUND
            )
        ]
    )
    fun delete(
        @Parameter(description = "Sauce ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val deleted = sauceService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @DELETE
    @Operation(
        summary = SAUCE_DELETE_ALL,
        description = "Removes all sauces from the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = NO_CONTENT,
                description = SAUCE_DELETED
            )
        ]
    )
    fun deleteAll(): Response {
        sauceService.deleteAll()
        return Response.noContent().build()
    }
}
