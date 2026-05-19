package sysout.openups.product

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import sysout.openups.auth.AuthTestHelper
import sysout.openups.config.seed.BaseResourceIT

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SauceResourceIT : BaseResourceIT() {

    @Test
    fun `should add and find sauce`() {
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        val id = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauceJson)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")
        RestAssured.given()
            .get("/sauces/$id")
            .then()
            .statusCode(200)
            .body("name", equalTo("Barbecue"))
    }

    @Test
    fun `should filter sauces by flavour`() {
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        RestAssured.given()
            .queryParam("flavour", "barbecue")
            .queryParam("paginated", false)
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("find { it.flavour == 'barbecue' }.name", equalTo("Barbecue"))
    }

    @Test
    fun `should return 404 for non-existing id`() {
        RestAssured.given().get("/sauces/00000000-0000-0000-0000-000000000000").then().statusCode(404)
    }

    @Test
    fun `should update sauce`() {
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces").then().extract().path<String>("id")
        val updateJson = """
            {"name":"Barbecue Atualizado","flavour":"spicy"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(updateJson).put("/sauces/$id").then().statusCode(200).body("name", equalTo("Barbecue Atualizado"))
    }

    @Test
    fun `should delete sauce`() {
        val adminToken = AuthTestHelper.getAdminToken()
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces").then().extract().path<String>("id")
        RestAssured.given()
            .header("Authorization", AuthTestHelper.buildAuthHeader(adminToken))
            .delete("/sauces/$id")
            .then().statusCode(204)
        RestAssured.given().get("/sauces/$id").then().statusCode(404)
    }

    @Test
    fun `should return empty list for filter with no results`() {
        RestAssured.given()
            .queryParam("flavour", "inexistente")
            .queryParam("paginated", false)
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should return paginated results with default values`() {
        repeat(7) { index ->
            val sauceJson = """
                {"name":"Sauce $index","flavour":"Flavor $index"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        }

        RestAssured.given()
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(5)) // default page size is 5
            .body("totalElements", equalTo(7))
            .body("totalPages", equalTo(2))
            .body("currentPage", equalTo(0))
            .body("pageSize", equalTo(5))
    }

    @Test
    fun `should return second page of results`() {
        // Add 7 sauces
        repeat(7) { index ->
            val sauceJson = """
                {"name":"Sauce $index","flavour":"Flavor $index"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        }

        RestAssured.given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(2)) // second page should have 2 items
            .body("totalElements", equalTo(7))
            .body("totalPages", equalTo(2))
            .body("currentPage", equalTo(1))
            .body("pageSize", equalTo(5))
    }

    @Test
    fun `should validate page size not exceeding maximum`() {
        repeat(12) { index ->
            val sauceJson = """
                {"name":"Sauce $index","flavour":"Flavor $index"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        }

        RestAssured.given()
            .queryParam("size", 20)
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("pageSize", lessThanOrEqualTo(10))
    }

    @Test
    fun `should return error for invalid page parameters`() {
        RestAssured.given()
            .queryParam("page", -1)
            .get("/sauces")
            .then()
            .statusCode(400)

        RestAssured.given()
            .queryParam("size", 0)
            .get("/sauces")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return paginated results with flavor filter`() {
        repeat(4) { index ->
            val sauceJson = """
                {"name":"Hot Sauce $index","flavour":"spicy hot"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        }

        repeat(3) { index ->
            val sauceJson = """
                {"name":"Sweet Sauce $index","flavour":"sweet"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        }

        RestAssured.given()
            .queryParam("flavour", "spicy")
            .queryParam("size", 2)
            .queryParam("page", 0)
            .get("/sauces")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(2))
            .body("totalElements", equalTo(4))
            .body("totalPages", equalTo(2))
            .body("data.every { it.flavour.contains('spicy') }", equalTo(true))
    }
}
