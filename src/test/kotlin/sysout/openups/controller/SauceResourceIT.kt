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
class SauceResourceIT {
    @BeforeEach
    fun cleanDb() {
        RestAssured.given().delete("/sauces")
    }

    @Test
    fun `deve adicionar e buscar sauce`() {
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
    fun `deve filtrar sauces por flavour`() {
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces")
        RestAssured.given().queryParam("flavour", "barbecue").get("/sauces").then().statusCode(200).body("size()", greaterThanOrEqualTo(1)).body("find { it.flavour == 'barbecue' }.name", equalTo("Barbecue"))
    }

    @Test
    fun `deve retornar 404 para id inexistente`() {
        RestAssured.given().get("/sauces/00000000-0000-0000-0000-000000000000").then().statusCode(404)
    }

    @Test
    fun `deve atualizar sauce`() {
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
    fun `deve deletar sauce`() {
        val sauceJson = """
            {"name":"Barbecue","flavour":"barbecue"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(sauceJson).post("/sauces").then().extract().path<String>("id")
        RestAssured.given().delete("/sauces/$id").then().statusCode(204)
        RestAssured.given().get("/sauces/$id").then().statusCode(404)
    }

    @Test
    fun `deve retornar lista vazia para filtro sem resultado`() {
        RestAssured.given().queryParam("flavour", "inexistente").get("/sauces").then().statusCode(200).body("size()", equalTo(0))
    }
}
