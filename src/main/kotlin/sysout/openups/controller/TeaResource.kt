package sysout.openups.controller

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sysout.openups.controller.dto.TeaDTO
import sysout.openups.controller.service.TeaService
import java.util.*

@Path("/teas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TeaResource @Inject constructor(
    private val teaService: TeaService
) {
    @GET
    fun listFiltered(
        @QueryParam("category") category: String?,
        @QueryParam("caffeineLevel") caffeineLevel: String?,
        @QueryParam("origin") origin: String?
    ) = teaService.filterTeas(category, caffeineLevel, origin)

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: UUID): Response {
        val tea = teaService.findById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(tea).build()
    }

    @POST
    fun add(dto: TeaDTO): Response {
        val created = teaService.add(dto)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: UUID, dto: TeaDTO): Response {
        val updated = teaService.update(id, dto) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(updated).build()
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: UUID): Response {
        val deleted = teaService.delete(id)
        return if (deleted) Response.noContent().build() else Response.status(Response.Status.NOT_FOUND).build()
    }
}
