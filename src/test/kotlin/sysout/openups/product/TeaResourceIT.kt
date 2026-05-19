package sysout.openups.product

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import sysout.openups.auth.AuthTestHelper
import sysout.openups.config.seed.BaseResourceIT

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeaResourceIT : BaseResourceIT() {

    @Test
    fun `should add and find tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","ingredients":[{"name":"Green Tea Leaves","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().statusCode(201).extract().path<String>("id")
        RestAssured.given().get("/teas/$id").then()
            .statusCode(200)
            .body("name", equalTo("Sencha"))
            .body("ingredients.size()", equalTo(1))
            .body("ingredients[0].name", equalTo("Green Tea Leaves"))
            .body("ingredients[0].quantity", equalTo(5.0f))
            .body("ingredients[0].unitOfMeasure", equalTo("GRAMS"))
    }

    @Test
    fun `should filter teas by category, caffeineLevel and origin`() {
        val teaJson = """
            {"name":"Chá de Camomila","origin":"brasil","description":"Chá calmante de camomila","ingredients":[{"name":"Chamomile Flowers","quantity":3.0,"unitOfMeasure":"GRAMS"}],"category":"HERBAL","caffeineLevel":"NONE"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        RestAssured.given()
            .queryParam("category", "HERBAL")
            .queryParam("caffeineLevel", "NONE")
            .queryParam("origin", "brasil")
            .queryParam("paginated", false)
            .get("/teas")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("find { it.name == 'Chá de Camomila' }.origin", equalTo("brasil"))
    }

    @Test
    fun `should return 404 for non-existing id`() {
        RestAssured.given().get("/teas/00000000-0000-0000-0000-000000000000").then().statusCode(404)
    }

    @Test
    fun `should update tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","ingredients":[{"name":"Green Tea Leaves","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        val updateJson = """
            {"name":"Sencha Atualizado","origin":"china","description":"Chá verde chinês","ingredients":[{"name":"Chinese Green Tea","quantity":6.0,"unitOfMeasure":"GRAMS"},{"name":"Jasmine","quantity":2.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"LOW"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(updateJson).put("/teas/$id").then()
            .statusCode(200)
            .body("name", equalTo("Sencha Atualizado"))
            .body("ingredients.size()", equalTo(2))
            .body("ingredients[0].name", equalTo("Chinese Green Tea"))
            .body("ingredients[1].name", equalTo("Jasmine"))
    }

    @Test
    fun `should delete tea`() {
        val adminToken = AuthTestHelper.getAdminToken()
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","ingredients":[{"name":"Green Tea Leaves","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        RestAssured.given()
            .header("Authorization", AuthTestHelper.buildAuthHeader(adminToken))
            .delete("/teas/$id")
            .then().statusCode(204)
        RestAssured.given().get("/teas/$id").then().statusCode(404)
    }

    @Test
    fun `should return empty list for filter with no results`() {
        val teaJson = """
            {"name":"Chá Mate","origin":"brasil","description":"Chá mate tostado","ingredients":[{"name":"Mate Leaves","quantity":7.0,"unitOfMeasure":"GRAMS"}],"category":"BLACK","caffeineLevel":"HIGH"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        RestAssured.given()
            .queryParam("category", "FLORAL")
            .queryParam("paginated", false)
            .get("/teas")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `should return paginated results with default values`() {
        repeat(7) { index ->
            val teaJson = """
                {"name":"Tea $index","origin":"japan","description":"Green tea $index","ingredients":[{"name":"Tea Leaves $index","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        }

        RestAssured.given()
            .get("/teas")
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
        repeat(7) { index ->
            val teaJson = """
                {"name":"Tea $index","origin":"japan","description":"Green tea $index","ingredients":[{"name":"Tea Leaves $index","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        }

        RestAssured.given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .get("/teas")
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
        // Add 12 teas
        repeat(12) { index ->
            val teaJson = """
                {"name":"Tea $index","origin":"japan","description":"Green tea $index","ingredients":[{"name":"Tea Leaves $index","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        }

        RestAssured.given()
            .queryParam("size", 20) // try to request more than max (10)
            .get("/teas")
            .then()
            .statusCode(200)
            .body("pageSize", lessThanOrEqualTo(10)) // should be limited to max size
    }

    @Test
    fun `should return error for invalid page parameters`() {
        RestAssured.given()
            .queryParam("page", -1)
            .get("/teas")
            .then()
            .statusCode(400)

        RestAssured.given()
            .queryParam("size", 0)
            .get("/teas")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return paginated results with filters`() {
        repeat(4) { index ->
            val teaJson = """
                {"name":"Green Tea $index","origin":"japan","description":"Green tea $index","ingredients":[{"name":"Green Tea Leaves $index","quantity":5.0,"unitOfMeasure":"GRAMS"}],"category":"GREEN","caffeineLevel":"MEDIUM"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        }

        repeat(3) { index ->
            val teaJson = """
                {"name":"Black Tea $index","origin":"india","description":"Black tea $index","ingredients":[{"name":"Black Tea Leaves $index","quantity":6.0,"unitOfMeasure":"GRAMS"}],"category":"BLACK","caffeineLevel":"HIGH"}
            """.trimIndent()
            RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        }

        RestAssured.given()
            .queryParam("category", "GREEN")
            .queryParam("size", 2)
            .queryParam("page", 0)
            .get("/teas")
            .then()
            .statusCode(200)
            .body("data.size()", equalTo(2))
            .body("data.every { it.category == 'GREEN' }", equalTo(true))
            .body("totalElements", greaterThanOrEqualTo(4))
            .body("totalPages", greaterThanOrEqualTo(2))
    }
}
