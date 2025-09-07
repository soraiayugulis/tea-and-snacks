package sysout.openups.controller

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnackResourceIT {
    @BeforeEach
    fun cleanDb() {
        RestAssured.given().delete("/snacks")
        RestAssured.given().delete("/sauces")
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
        RestAssured.given()
            .queryParam("vegan", true)
            .queryParam("paginated", false)
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
            {"name":"Snack","description":"Description","flavor":"sweet","vegan":true,"sides":[]}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        RestAssured.given()
            .queryParam("flavour", "sweet")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("find { it.flavor == 'sweet' }.name", equalTo("Snack"))
    }

    @Test
    fun `should return empty list for filter with no results`() {
        val snackJson = """
            {"name":"Snack","description":"Description","flavor":"sweet","vegan":true,"sides":[]}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        RestAssured.given()
            .queryParam("flavour", "salty")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should add sauce to snack successfully and verify sauce list`() {
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

        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(1))
            .body("sides[0]", equalTo(sauceId))

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

        RestAssured.given()
            .contentType(ContentType.JSON)
            .post("/snacks/$snackId/sauces/$sauceId")
            .then()
            .statusCode(200)
            .body("sides.size()", equalTo(1))

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

    @Test
    fun `should filter snacks by sauce flavour`() {
        val sauceJson = """
            {"name":"Cheese","description":"Cheese sauce","flavour":"american cheese"}
        """.trimIndent()
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauceJson)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val snackJson = """
            {"name":"Batata Frita","description":"Com queijo","flavor":"batata","vegan":false,"sides":["$sauceId"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snackJson)
            .post("/snacks")
            .then()
            .statusCode(201)

        RestAssured.given()
            .queryParam("sauce", "cheese")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Batata Frita"))
    }

    @Test
    fun `should filter snacks case insensitive by sauce flavour`() {
        val sauceJson = """
            {"name":"BBQ","description":"Barbecue sauce","flavour":"Spicy BBQ"}
        """.trimIndent()
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauceJson)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val snackJson = """
            {"name":"Wings","description":"Chicken wings","flavor":"frango","vegan":false,"sides":["$sauceId"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snackJson)
            .post("/snacks")
            .then()
            .statusCode(201)

        RestAssured.given()
            .queryParam("sauce", "bbq")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Wings"))

        RestAssured.given()
            .queryParam("sauce", "BBQ")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Wings"))
    }

    @Test
    fun `should filter snacks by partial sauce flavour match`() {
        val sauce1Json = """
            {"name":"Mayo Garlic","description":"Mayo with garlic","flavour":"garlic mayo"}
        """.trimIndent()
        val sauce1Id = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauce1Json)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val sauce2Json = """
            {"name":"Mayo Herbs","description":"Mayo with herbs","flavour":"herb mayo"}
        """.trimIndent()
        val sauce2Id = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauce2Json)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val snack1Json = """
            {"name":"Batata Mayo 1","description":"Com maionese de alho","flavor":"batata","vegan":false,"sides":["$sauce1Id"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snack1Json)
            .post("/snacks")
            .then()
            .statusCode(201)

        val snack2Json = """
            {"name":"Batata Mayo 2","description":"Com maionese de ervas","flavor":"batata","vegan":false,"sides":["$sauce2Id"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snack2Json)
            .post("/snacks")
            .then()
            .statusCode(201)

        RestAssured.given()
            .queryParam("sauce", "mayo")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
    }

    @Test
    fun `should combine all filters - vegan, flavour and sauce`() {
        val veganSauceJson = """
            {"name":"Vegan Mayo","description":"Plant based mayo","flavour":"vegan mayo"}
        """.trimIndent()
        val veganSauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(veganSauceJson)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val veganSnackJson = """
            {"name":"Salada Vegana","description":"Com molho vegano","flavor":"verde","vegan":true,"sides":["$veganSauceId"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(veganSnackJson)
            .post("/snacks")
            .then()
            .statusCode(201)

        RestAssured.given()
            .queryParam("vegan", true)
            .queryParam("flavour", "verde")
            .queryParam("sauce", "mayo")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Salada Vegana"))
            .body("[0].vegan", equalTo(true))
            .body("[0].flavor", equalTo("verde"))
    }

    @Test
    fun `should return empty list when no snacks match filter combination`() {
        val sauceJson = """
            {"name":"Mostarda","description":"Mostarda tradicional","flavour":"mostarda"}
        """.trimIndent()
        val sauceId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sauceJson)
            .post("/sauces")
            .then()
            .statusCode(201)
            .extract().path<String>("id")

        val snackJson = """
            {"name":"Cachorro Quente","description":"Cachorro quente completo","flavor":"salsicha","vegan":false,"sides":["$sauceId"]}
        """.trimIndent()
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(snackJson)
            .post("/snacks")
            .then()
            .statusCode(201)

        RestAssured.given()
            .queryParam("vegan", true)
            .queryParam("sauce", "mostarda")
            .queryParam("paginated", false)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should return paginated results with default values`() {
        repeat(7) { index ->
            val snackJson = """
                {"name":"Snack $index","description":"Description $index","flavor":"flavor $index","vegan":${index % 2 == 0},"sides":[]}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        }

        RestAssured.given()
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(5)) // Default page size is 5
            .body("totalElements", equalTo(7))
            .body("totalPages", equalTo(2))
            .body("currentPage", equalTo(0))
            .body("pageSize", equalTo(5))
    }

    @Test
    fun `should return second page of results`() {
        repeat(7) { index ->
            val snackJson = """
                {"name":"Snack $index","description":"Description $index","flavor":"flavor $index","vegan":${index % 2 == 0},"sides":[]}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        }

        RestAssured.given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .get("/snacks")
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
        // Add 12 snacks
        repeat(12) { index ->
            val snackJson = """
                {"name":"Snack $index","description":"Description $index","flavor":"flavor $index","vegan":${index % 2 == 0},"sides":[]}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        }

        RestAssured.given()
            .queryParam("size", 20)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("pageSize", lessThanOrEqualTo(10)) // should be limited to max size
    }

    @Test
    fun `should return error for invalid page parameters`() {
        RestAssured.given()
            .queryParam("page", -1)
            .get("/snacks")
            .then()
            .statusCode(400)

        RestAssured.given()
            .queryParam("size", 0)
            .get("/snacks")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return paginated results with vegan filter`() {
        repeat(4) { index ->
            val snackJson = """
                {"name":"Vegan Snack $index","description":"Vegan $index","flavor":"veggie $index","vegan":true,"sides":[]}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        }

        repeat(3) { index ->
            val snackJson = """
                {"name":"Non-vegan Snack $index","description":"Non-vegan $index","flavor":"meat $index","vegan":false,"sides":[]}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks")
        }

        RestAssured.given()
            .queryParam("vegan", true)
            .queryParam("size", 2)
            .queryParam("page", 0)
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(2))
            .body("totalElements", equalTo(4))
            .body("totalPages", equalTo(2))
            .body("data.every { it.vegan == true }", equalTo(true))
    }
}
