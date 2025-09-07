package sysout.openups.config.seed

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sysout.openups.config.seed.seeder.DatabaseSeeder

@Path("/api/seed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SeedResource @Inject constructor(
    private val databaseSeeder: DatabaseSeeder
) {
    @POST
    @Path("/initialize")
    fun initializeSeed(): Response {
        databaseSeeder.seed()
        return Response.ok(mapOf(
            "message" to "Base de dados inicializada com sucesso",
            "seeded" to true
        )).build()
    }

    @POST
    @Path("/reset")
    fun resetSeed(): Response {
        databaseSeeder.reset()
        return Response.ok(mapOf(
            "message" to "Base de dados resetada com sucesso",
            "seeded" to false
        )).build()
    }

    @GET
    @Path("/status")
    fun getStatus(): Response {
        return Response.ok(mapOf(
            "seeded" to databaseSeeder.isSeeded()
        )).build()
    }
}