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
import sysout.openups.controller.dto.SauceDTO
import sysout.openups.controller.service.SauceService
import java.util.*

@Path("/sauces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sauces", description = "Sauce operations")
class SauceResource @Inject constructor(
    private val sauceService: SauceService
) {
    @GET
    @Operation(summary = "List sauces with filters",
               description = "Returns a list of sauces that can be filtered by flavor")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "List of filtered sauces",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SauceDTO::class))]),
            APIResponse(responseCode = "404", description = "No sauces found matching the criteria")
        ]
    )
    fun listFiltered(
        @Parameter(description = "Filter by flavor") @QueryParam("flavour") flavour: String?
    ) = sauceService.filterSauces(flavour)

    @GET
    @Path("/{id}")
    @Operation(summary = "Find sauce by ID", description = "Returns a specific sauce by its ID")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Sauce found",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SauceDTO::class))]),
            APIResponse(responseCode = "404", description = "Sauce not found")
        ]
    )
    fun getById(@Parameter(description = "Sauce ID", required = true) @PathParam("id") id: UUID): Response {
        val sauce = sauceService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(sauce).build()
    }

    @POST
    @Operation(summary = "Add a new sauce", description = "Creates a new sauce in the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "201", description = "Sauce created successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SauceDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid sauce data provided")
        ]
    )
    fun add(@Parameter(description = "Sauce data to be added", required = true) dto: SauceDTO): Response {
        val created = sauceService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a sauce", description = "Updates an existing sauce's data")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Sauce updated successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = SauceDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid sauce data provided"),
            APIResponse(responseCode = "404", description = "Sauce not found")
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
    @Operation(summary = "Delete a sauce", description = "Removes a sauce from the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "204", description = "Sauce deleted successfully"),
            APIResponse(responseCode = "404", description = "Sauce not found")
        ]
    )
    fun delete(@Parameter(description = "Sauce ID", required = true) @PathParam("id") id: UUID): Response {
        val deleted = sauceService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }
}
