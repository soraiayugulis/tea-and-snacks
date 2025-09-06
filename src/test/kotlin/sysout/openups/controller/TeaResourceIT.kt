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
class TeaResourceIT {
    @BeforeEach
    fun cleanDb() {
        RestAssured.given().delete("/teas")
    }

    @Test
    fun `deve adicionar e buscar tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"green","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().statusCode(201).extract().path<String>("id")
        RestAssured.given().get("/teas/$id").then().statusCode(200).body("name", equalTo("Sencha"))
    }

    @Test
    fun `deve filtrar teas por category, caffeineLevel e origin`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"green","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        RestAssured.given().queryParam("category", "green").queryParam("caffeineLevel", "MEDIUM").queryParam("origin", "japan")
            .get("/teas")
            .then().statusCode(200).body("size()", greaterThanOrEqualTo(1)).body("find { it.name == 'Sencha' }.origin", equalTo("japan"))
    }

    @Test
    fun `deve retornar 404 para id inexistente`() {
        RestAssured.given().get("/teas/00000000-0000-0000-0000-000000000000").then().statusCode(404)
    }

    @Test
    fun `deve atualizar tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"green","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        val updateJson = """
            {"name":"Sencha Atualizado","origin":"china","description":"Chá verde chinês","category":"green","caffeineLevel":"LOW"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(updateJson).put("/teas/$id").then().statusCode(200).body("name", equalTo("Sencha Atualizado"))
    }

    @Test
    fun `deve deletar tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"green","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        RestAssured.given().delete("/teas/$id").then().statusCode(204)
        RestAssured.given().get("/teas/$id").then().statusCode(404)
    }

    @Test
    fun `deve retornar lista vazia para filtro sem resultado`() {
        RestAssured.given().queryParam("category", "inexistente").get("/teas").then().statusCode(200).body("size()", equalTo(0))
    }
}
