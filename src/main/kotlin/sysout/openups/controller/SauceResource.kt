package sysout.openups.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sysout.openups.controller.dto.SauceDTO
import sysout.openups.controller.service.SauceService
import java.util.*

@Path("/sauces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SauceResource @Inject constructor(
    private val sauceService: SauceService
) {
    @GET
    fun listAll() = sauceService.listAll()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: UUID): Response {
        val sauce = sauceService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(sauce).build()
    }

    @POST
    fun add(dto: SauceDTO): Response {
        val created = sauceService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: UUID, dto: SauceDTO): Response {
        val updated = sauceService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: UUID): Response {
        val deleted = sauceService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }
}
