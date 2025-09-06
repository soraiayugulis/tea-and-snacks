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
import sysout.openups.controller.dto.TeaDTO
import sysout.openups.controller.service.TeaService
import java.util.*

@Path("/teas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teas", description = "Tea operations")
class TeaResource @Inject constructor(
    private val teaService: TeaService
) {
    @GET
    @Operation(summary = "List teas with filtering",
               description = "Returns a list of teas that can be filtered by category, caffeine level or origin")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "List of filtered teas",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = TeaDTO::class))]),
            APIResponse(responseCode = "404", description = "No teas found matching the criteria")
        ]
    )
    fun listFiltered(
        @Parameter(description = "Tea category", schema = Schema(enumeration = ["BLACK", "GREEN", "HERBAL", "OOLONG", "WHITE", "FLORAL", "OTHER"]))
        @QueryParam("category") category: String?,

        @Parameter(description = "Caffeine level", schema = Schema(enumeration = ["NONE", "LOW", "MEDIUM", "HIGH"]))
        @QueryParam("caffeineLevel") caffeineLevel: String?,

        @Parameter(description = "Country of origin")
        @QueryParam("origin") origin: String?
    ) = teaService.filterTeas(category, caffeineLevel, origin)

    @GET
    @Path("/{id}")
    @Operation(summary = "Find tea by ID", description = "Returns a specific tea by its ID")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Tea found",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = TeaDTO::class))]),
            APIResponse(responseCode = "404", description = "Tea not found")
        ]
    )
    fun getById(@Parameter(description = "Tea ID", required = true) @PathParam("id") id: UUID): Response {
        val tea = teaService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(tea).build()
    }

    @POST
    @Operation(summary = "Add a new tea", description = "Creates a new tea in the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "201", description = "Tea created successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = TeaDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid tea data provided")
        ]
    )
    fun add(@Parameter(description = "Tea data to be added", required = true) dto: TeaDTO): Response {
        val created = teaService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a tea", description = "Updates an existing tea's data")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Tea updated successfully",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = Schema(implementation = TeaDTO::class))]),
            APIResponse(responseCode = "400", description = "Invalid tea data provided"),
            APIResponse(responseCode = "404", description = "Tea not found")
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
    @Operation(summary = "Delete a tea", description = "Removes a tea from the system")
    @APIResponses(
        value = [
            APIResponse(responseCode = "204", description = "Tea deleted successfully"),
            APIResponse(responseCode = "404", description = "Tea not found")
        ]
    )
    fun delete(@Parameter(description = "Tea ID", required = true) @PathParam("id") id: UUID): Response {
        val deleted = teaService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @DELETE
    @Operation(summary = "Delete teas with filtering",
               description = "Removes teas from the system, optionally filtered by category, caffeine level, or origin")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "Teas deleted successfully, returns number of deleted items",
                       content = [Content(mediaType = MediaType.APPLICATION_JSON)]),
            APIResponse(responseCode = "204", description = "No teas found matching the criteria")
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
