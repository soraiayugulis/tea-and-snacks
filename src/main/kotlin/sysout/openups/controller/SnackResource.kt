package sysout.openups.controller

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
import sysout.openups.controller.common.Constants.Http.Status.BAD_REQUEST
import sysout.openups.controller.common.Constants.Http.Status.CREATED
import sysout.openups.controller.common.Constants.Http.Status.NOT_FOUND
import sysout.openups.controller.common.Constants.Http.Status.NO_CONTENT
import sysout.openups.controller.common.Constants.Http.Status.OK
import sysout.openups.controller.common.Constants.List.SAUCE_BY_SNACK
import sysout.openups.controller.common.Constants.List.SNACK_FILTERED
import sysout.openups.controller.common.Constants.Message.Error.Entity.SAUCE_SNACK_NOT_FOUND
import sysout.openups.controller.common.Constants.Message.Error.Entity.SNACK_INVALID_DATA
import sysout.openups.controller.common.Constants.Message.Error.Entity.SNACK_NOT_FOUND
import sysout.openups.controller.common.Constants.Message.Success.Entity.SAUCE_ADDED_TO_SNACK
import sysout.openups.controller.common.Constants.Message.Success.Entity.SAUCE_REMOVED_FROM_SNACK
import sysout.openups.controller.common.Constants.Message.Success.Entity.SNACK_CREATED
import sysout.openups.controller.common.Constants.Message.Success.Entity.SNACK_DELETED
import sysout.openups.controller.common.Constants.Message.Success.Entity.SNACK_FOUND
import sysout.openups.controller.common.Constants.Message.Success.Entity.SNACK_UPDATED
import sysout.openups.controller.common.Constants.Operation.SAUCE_REMOVE_FROM_SNACK
import sysout.openups.controller.common.Constants.Operation.SAUCE_TO_SNACK
import sysout.openups.controller.common.Constants.Operation.SNACK_ADD
import sysout.openups.controller.common.Constants.Operation.SNACK_DELETE
import sysout.openups.controller.common.Constants.Operation.SNACK_FIND_BY_ID
import sysout.openups.controller.common.Constants.Operation.SNACK_UPDATE
import sysout.openups.controller.dto.SnackDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.service.SnackService
import java.util.*

@Path("/snacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Snacks", description = "Snack operations")
class SnackResource @Inject constructor(
    private val snackService: SnackService
) {
    @GET
    @Operation(
        summary = SNACK_FILTERED,
        description = "Returns a list of snacks that can be filtered by vegan option or flavor"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SNACK_FILTERED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            )
        ]
    )
    fun listAll(
        @Parameter(description = "Filter by vegan snacks") @QueryParam("vegan") vegan: Boolean?,
        @Parameter(description = "Filter by flavor") @QueryParam("flavour") flavour: String?
    ): List<SnackDTO> = snackService.listAll(vegan, flavour)

    @GET
    @Path("/{id}")
    @Operation(
        summary = SNACK_FIND_BY_ID,
        description = "Returns a specific snack by its ID"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SNACK_FOUND,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SNACK_NOT_FOUND
            )
        ]
    )
    fun getById(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val snack = snackService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(snack).build()
    }

    @POST
    @Operation(
        summary = SNACK_ADD,
        description = "Creates a new snack in the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = CREATED,
                description = SNACK_CREATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = SNACK_INVALID_DATA
            )
        ]
    )
    fun add(
        @Parameter(description = "Snack data to be added", required = true) dto: SnackDTO
    ): Response {
        val created = snackService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = SNACK_UPDATE,
        description = "Updates an existing snack's data"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SNACK_UPDATED,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SNACK_NOT_FOUND
            ),
            APIResponse(
                responseCode = BAD_REQUEST,
                description = SNACK_INVALID_DATA
            )
        ]
    )
    fun update(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID,
        @Parameter(description = "New snack data", required = true) dto: SnackDTO
    ): Response {
        val updated = snackService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = SNACK_DELETE,
        description = "Removes a snack from the system"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = NO_CONTENT,
                description = SNACK_DELETED
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SNACK_NOT_FOUND
            )
        ]
    )
    fun delete(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID
    ): Response {
        val deleted = snackService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @GET
    @Path("/{id}/sauces")
    @Operation(
        summary = SAUCE_BY_SNACK,
        description = "Returns a list of sauces associated with a specific snack"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_BY_SNACK,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = Sauce::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SNACK_NOT_FOUND
            )
        ]
    )
    fun getSauces(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID
    ): Response {
        try {
            val sauces = snackService.getSauces(id)
            return Response.ok(sauces).build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @POST
    @Path("/{id}/sauces/{sauceId}")
    @Operation(
        summary = SAUCE_TO_SNACK,
        description = "Add a specific sauce to a specific snack"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_ADDED_TO_SNACK,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SAUCE_SNACK_NOT_FOUND
            )
        ]
    )
    fun addSauce(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID,
        @Parameter(description = "Sauce ID", required = true) @PathParam("sauceId") sauceId: UUID
    ): Response {
        try {
            val updatedSnack = snackService.addSauce(id, sauceId)
            return Response.ok(updatedSnack).build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @DELETE
    @Path("/{id}/sauces/{sauceId}")
    @Operation(
        summary = SAUCE_REMOVE_FROM_SNACK,
        description = "Removes a specific sauce from a specific snack"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = OK,
                description = SAUCE_REMOVED_FROM_SNACK,
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = SnackDTO::class)
                )]
            ),
            APIResponse(
                responseCode = NOT_FOUND,
                description = SAUCE_SNACK_NOT_FOUND
            )
        ]
    )
    fun removeSauce(
        @Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID,
        @Parameter(description = "Sauce ID", required = true) @PathParam("sauceId") sauceId: UUID
    ): Response {
        try {
            val updatedSnack = snackService.removeSauce(id, sauceId)
            return Response.ok(updatedSnack).build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }
}
