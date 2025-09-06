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
    fun `deve adicionar e buscar snack`() {
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
    fun `deve filtrar snacks por vegan`() {
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
    fun `deve retornar 404 para id inexistente`() {
        RestAssured.given()
            .get("/snacks/00000000-0000-0000-0000-000000000000")
            .then()
            .statusCode(404)
    }

    @Test
    fun `deve atualizar snack`() {
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
    fun `deve deletar snack`() {
        val snackJson = """
            {"name":"Coxinha","description":"Frango","flavor":"frango","vegan":false,"sides":[]}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(snackJson).post("/snacks").then().extract().path<String>("id")
        RestAssured.given().delete("/snacks/$id").then().statusCode(204)
        RestAssured.given().get("/snacks/$id").then().statusCode(404)
    }

    @Test
    fun `deve filtrar snacks por flavour`() {
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
    fun `deve retornar lista vazia para filtro sem resultado`() {
        RestAssured.given().queryParam("flavour", "inexistente")
            .get("/snacks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }
}
