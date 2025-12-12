package sysout.openups.config.seed

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import sysout.openups.config.seed.seeder.DatabaseSeeder
import sysout.openups.product.repository.SauceRepository
import sysout.openups.product.repository.SnackRepository
import sysout.openups.product.repository.TeaRepository

@Path("/api/seed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SeedResource @Inject constructor(
    private val databaseSeeder: DatabaseSeeder,
    private val teaRepository: TeaRepository,
    private val snackRepository: SnackRepository,
    private val sauceRepository: SauceRepository
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

    @GET
    @Path("/data")
    fun getSeedData(): Response {
        if (!databaseSeeder.isSeeded()) {
            return Response.ok(mapOf(
                "message" to "Database not seeded"
            )).build()
        }

        val teas = teaRepository.listAll()
        val snacks = snackRepository.listAll()
        val sauces = sauceRepository.listAll()

        return Response.ok(mapOf(
            "teas" to teas,
            "snacks" to snacks,
            "sauces" to sauces,
            "total" to mapOf(
                "teas" to teas.size,
                "snacks" to snacks.size,
                "sauces" to sauces.size
            )
        )).build()
    }
}
