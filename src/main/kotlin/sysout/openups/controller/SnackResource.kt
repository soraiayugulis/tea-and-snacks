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
    @Operation(summary = "List snacks with filters",
               description = "Returns a list of snacks that can be filtered by vegan option or flavor")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "List of filtered snacks",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SnackDTO::class))]),
            APIResponse(responseCode = "404", description = "No snacks found matching the criteria")
        ]
    )
    fun listAll(
        @Parameter(description = "Filter by vegan snacks") @QueryParam("vegan") vegan: Boolean?,
        @Parameter(description = "Filter by flavor") @QueryParam("flavour") flavour: String?
    ): List<SnackDTO> = snackService.listAll(vegan, flavour)

    @GET
    @Path("/{id}")
    @Operation(summary = "Find snack by ID", description = "Returns a specific snack by its ID")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Snack found",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SnackDTO::class))]),
            APIResponse(responseCode = "404", description = "Snack not found")
        ]
    )
    fun getById(@Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID): Response {
        val snack = snackService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(snack).build()
    }

    @POST
    @Operation(summary = "Add a new snack", description = "Creates a new snack in the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "201", description = "Snack created successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SnackDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid snack data provided")
        ]
    )
    fun add(@Parameter(description = "Snack data to be added", required = true) dto: SnackDTO): Response {
        val created = snackService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a snack", description = "Updates an existing snack's data")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Snack updated successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SnackDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid snack data provided"),
            APIResponse(responseCode = "404", description = "Snack not found")
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
    @Operation(summary = "Delete a snack", description = "Removes a snack from the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "204", description = "Snack deleted successfully"),
            APIResponse(responseCode = "404", description = "Snack not found")
        ]
    )
    fun delete(@Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID): Response {
        val deleted = snackService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @GET
    @Path("/{id}/sauces")
    @Operation(summary = "List sauces for a snack", description = "Returns the list of sauces associated with a specific snack")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "List of snack sauces",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = Sauce::class))]),
            APIResponse(responseCode = "404", description = "Snack not found or no sauces associated with this snack")
        ]
    )
    fun getSauces(@Parameter(description = "Snack ID", required = true) @PathParam("id") id: UUID): List<Sauce> {
        return snackService.getSauces(id)
    }
}
