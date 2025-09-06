package sysout.openups.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sysout.openups.controller.dto.SnackDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.service.SnackService
import java.util.*

@Path("/snacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SnackResource @Inject constructor(
    private val snackService: SnackService
) {
    @GET
    fun listAll(
        @QueryParam("vegan") vegan: Boolean?,
        @QueryParam("flavour") flavour: String?
    ): List<SnackDTO> = snackService.listAll(vegan, flavour)

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: UUID): Response {
        val snack = snackService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(snack).build()
    }

    @POST
    fun add(dto: SnackDTO): Response {
        val created = snackService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: UUID, dto: SnackDTO): Response {
        val updated = snackService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: UUID): Response {
        val deleted = snackService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }

    @GET
    @Path("/{id}/sauces")
    fun getSauces(@PathParam("id") id: UUID): List<Sauce> {
        return snackService.getSauces(id)
    }
}
