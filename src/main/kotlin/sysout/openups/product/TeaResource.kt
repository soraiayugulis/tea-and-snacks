package sysout.openups.product

import jakarta.annotation.security.RolesAllowed
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
import sysout.openups.common.Constants.List.TEA_FILTERED
import sysout.openups.common.Constants.Message.Error.Entity.TEAS_NOT_FOUND
import sysout.openups.common.Constants.Message.Error.Entity.TEA_INVALID_DATA
import sysout.openups.common.Constants.Message.Error.Entity.TEA_NOT_FOUND
import sysout.openups.common.Constants.Message.Success.Entity.TEAS_DELETED
import sysout.openups.common.Constants.Message.Success.Entity.TEA_CREATED
import sysout.openups.common.Constants.Message.Success.Entity.TEA_DELETED
import sysout.openups.common.Constants.Message.Success.Entity.TEA_FOUND
import sysout.openups.common.Constants.Message.Success.Entity.TEA_UPDATED
import sysout.openups.common.Constants.Operation.TEA_ADD
import sysout.openups.common.Constants.Operation.TEA_DELETE
import sysout.openups.common.Constants.Operation.TEA_DELETE_FILTER
import sysout.openups.common.Constants.Operation.TEA_FIND_BY_ID
import sysout.openups.common.Constants.Operation.TEA_UPDATE
import sysout.openups.common.Constants.Pagination.Params.PAGE_NUMBER_PARAM
import sysout.openups.common.Constants.Pagination.Params.PAGE_SIZE_PARAM
import sysout.openups.common.pagination.PaginatedResponse
import sysout.openups.product.dto.TeaDTO
import sysout.openups.product.service.TeaService
import java.util.*

@Path("/teas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teas", description = "Tea operations")
class TeaResource @Inject constructor(
    private val teaService: TeaService
) {
    @GET
    @Operation(
        summary = TEA_FILTERED,
        description = "Returns a paginated list of teas that can be filtered by category, caffeine level, and origin"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = TEA_FILTERED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = PaginatedResponse::class)
                )]
            )
        ]
    )
    fun list(
        @Parameter(description = "Tea category", schema = Schema(enumeration = ["BLACK", "GREEN", "HERBAL", "OOLONG", "WHITE", "FLORAL", "OTHER"]))
        @QueryParam("category") category: String?,

        @Parameter(description = "Caffeine level", schema = Schema(enumeration = ["NONE", "LOW", "MEDIUM", "HIGH"]))
        @QueryParam("caffeineLevel") caffeineLevel: String?,

        @Parameter(description = "Country of origin")
        @QueryParam("origin") origin: String?,

        @Parameter(description = "Page size (default: 5)")
        @QueryParam(PAGE_SIZE_PARAM) size: Int?,

        @Parameter(description = "Page number (default: 0)")
        @QueryParam(PAGE_NUMBER_PARAM) page: Int?,

        @Parameter(description = "Use pagination (default: true)")
        @QueryParam("paginated") paginated: Boolean?
    ): Response {
        val result = if (paginated ?: true) {
            teaService.filterTeas(category, caffeineLevel, origin, page ?: 0, size ?: 5)
        } else {
            teaService.filterTeas(category, caffeineLevel, origin)
        }
        return Response.ok(result).build()
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = TEA_FIND_BY_ID,
        description = "Returns a specific tea by its ID"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = TEA_FOUND,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = TeaDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = TEA_NOT_FOUND
            )
        ]
    )
    fun getById(
        @Parameter(description = "Tea ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val tea = teaService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(tea).build()
    }

    @POST
    @Operation(
        summary = TEA_ADD,
        description = "Creates a new tea in the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = CREATED,
                description = TEA_CREATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = TeaDTO::class)
                )]
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = TEA_INVALID_DATA
            )
        ]
    )
    fun add(
        @Parameter(description = "Tea data to be added", required = true) dto: TeaDTO
    ): Response {
        val created = teaService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = TEA_UPDATE,
        description = "Updates an existing tea's data"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = TEA_UPDATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = TeaDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = TEA_NOT_FOUND
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = TEA_INVALID_DATA
            )
        ]
    )
    fun update(
        @Parameter(description = "Tea ID", required = true) @PathParam("id") id: UUID,
        @Parameter(description = "New tea data", required = true) dto: TeaDTO
    ): Response {
        val updated = teaService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN", "MANAGER")
    @Operation(
        summary = TEA_DELETE,
        description = "Removes a tea from the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = NO_CONTENT,
                description = TEA_DELETED
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = TEA_NOT_FOUND
            )
        ]
    )
    fun delete(
        @Parameter(description = "Tea ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val deleted = teaService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Operation(
        summary = TEA_DELETE_FILTER,
        description = "Removes teas from the system, optionally filtered by category, caffeine level, or origin (Admin only)"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = TEAS_DELETED,
                content = [Content(mediaType = MediaType.APPLICATION_JSON)]
            ),
            APIResponse(
                responseCode = NO_CONTENT,
                description = TEAS_NOT_FOUND
            )
        ]
    )
    fun deleteFiltered(
        @Parameter(description = "Tea category", schema = Schema(enumeration = ["BLACK", "GREEN", "HERBAL", "OOLONG", "WHITE", "FLORAL", "OTHER"]))
        @QueryParam("category") category: String?,

        @Parameter(description = "Caffeine level", schema = Schema(enumeration = ["NONE", "LOW", "MEDIUM", "HIGH"]))
        @QueryParam("caffeineLevel") caffeineLevel: String?,

        @Parameter(description = "Country of origin")
        @QueryParam("origin") origin: String?
    ): Response {
        val count = teaService.deleteFiltered(category, caffeineLevel, origin)
        return if (count > 0) {
            Response.ok(mapOf("deletedCount" to count)).build()
        } else {
            Response.noContent().build()
        }
    }
}
