package sysout.openups.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnackResourceIT {
    @BeforeEach
    fun cleanDb() {
        RestAssured.given().delete("/snacks")
    }

    @Test
    fun `should add and find snack`() {
        val snackJson = """
            {"name":"Coxinha","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        val id = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snackJson)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract().path<String>("id")
        RestAssured.given()
            .get("/snacks/$id")
            .then()
            .statusCode(200)
            .body("name", equalTo("Coxinha"))
    }

    @Test
    fun `should filter snacks by vegan`() {
        val snackJson = """
            {"name":"Kibe Vegano","description":"Soja","flavor":"soja","vegan":true,"sides":[]}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        RestAssured.given().queryParam("vegan", true)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("find { it.vegan == true }.name", equalTo("Kibe Vegano"))
    }

    @Test
    fun `should return 404 for non-existing id`() {
        RestAssured.given()
            .get("/snacks/00000000-0000-0000-0000-000000000000")
            .then()
            .statusCode(404)
    }

    @Test
    fun `should update snack`() {
        val snackJson = """
            {"name":"Coxinha","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks").then().extract().path<String>("id")
        val updateJson = """
            {"name":"Coxinha Atualizada","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(updateJson).put("/snacks/$id")
            .then().statusCode(200)
            .body("name", equalTo("Coxinha Atualizada"))
    }

    @Test
    fun `should delete snack`() {
        val snackJson = """
            {"name":"Coxinha","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks").then().extract().path<String>("id")
        RestAssured.given().delete("/snacks/$id").then().statusCode(204)
        RestAssured.given().get("/snacks/$id").then().statusCode(404)
    }

    @Test
    fun `should filter snacks by flavor`() {
        val snackJson = """
            {"name":"Coxinha","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        RestAssured.given().queryParam("flavour", "frango")
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("find { it.flavor == 'frango' }.name", equalTo("Coxinha"))
    }

    @Test
    fun `should return empty list for filter with no results`() {
        RestAssured.given().queryParam("flavour", "inexistente")
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should add sauce to snack successfully and verify sauce list`() {
        // Create snack
        val snackId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Hot Dog",
                    "description": "Classic hot dog",
                    "flavor": "meat",
                    "vegan": false,
                    "sides": []
                }
            """)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Create sauce
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Mustard",
                    "description": "Yellow mustard"
                }
            """)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Add sauce to snack
        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(1))
            .body("sides[0]", equalTo(sauceId))

        // Verify sauce is in the snack's sauce list
        RestAssured.given()
            .get("/snacks/$snackId/sauces")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].id", equalTo(sauceId))
            .body("[0].name", equalTo("Mustard"))
    }

    @Test
    fun `should not add duplicate sauce to snack`() {
        // Create snack
        val snackId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Hot Dog",
                    "description": "Classic hot dog",
                    "flavor": "meat",
                    "vegan": false,
                    "sides": []
                }
            """)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Create sauce
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Mustard",
                    "description": "Yellow mustard"
                }
            """)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Add sauce first time
        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(1))

        // Try to add same sauce again
        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(1))
    }

    @Test
    fun `should return 404 and proper message when adding sauce to non-existent snack`() {
        val nonExistentSnackId = "00000000-0000-0000-0000-000000000000"
        val nonExistentSauceId = "00000000-0000-0000-0000-000000000000"

        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$nonExistentSnackId/sauces/$nonExistentSauceId")
            .then()
            .statusCode(404)
            .body("message", equalTo("Snack not found"))
    }

    @Test
    fun `should return 404 and proper message when adding non-existent sauce`() {
        // Create snack
        val snackId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Hot Dog",
                    "description": "Classic hot dog",
                    "flavor": "meat",
                    "vegan": false,
                    "sides": []
                }
            """)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        val nonExistentSauceId = "00000000-0000-0000-0000-000000000000"

        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$nonExistentSauceId")
            .then()
            .statusCode(404)
            .body("message", equalTo("Sauce not found"))
    }

    @Test
    fun `should remove sauce from snack successfully`() {
        // Create snack
        val snackId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Hot Dog",
                    "description": "Classic hot dog",
                    "flavor": "meat",
                    "vegan": false,
                    "sides": []
                }
            """)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Create sauce
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Mustard",
                    "description": "Yellow mustard"
                }
            """)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        // Add sauce to snack
        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)

        // Remove sauce
        RestAssured.given()
            .contentType(ContentType.JSON)
            .delete("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(0))

        // Verify sauce list is empty
        RestAssured.given()
            .get("/snacks/$snackId/sauces")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should return 404 when removing sauce from non-existent snack`() {
        val nonExistentSnackId = "00000000-0000-0000-0000-000000000000"
        val nonExistentSauceId = "00000000-0000-0000-0000-000000000000"

        RestAssured.given()
            .contentType(ContentType.JSON)
            .delete("/snacks/$nonExistentSnackId/sauces/$nonExistentSauceId")
            .then()
            .statusCode(404)
            .body("message", equalTo("Snack not found"))
    }

    @Test
    fun `should handle removing non-existent sauce gracefully`() {
        // Create snack
        val snackId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Hot Dog",
                    "description": "Classic hot dog",
                    "flavor": "meat",
                    "vegan": false,
                    "sides": []
                }
            """)
            .post("/snacks")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

        val nonExistentSauceId = "00000000-0000-0000-0000-000000000000"

        RestAssured.given()
            .contentType(ContentType.JSON)
            .delete("/snacks/$snackId/sauces/$nonExistentSauceId")
            .then()
            .statusCode(404)
            .body("message", equalTo("Sauce not found"))
    }
}
