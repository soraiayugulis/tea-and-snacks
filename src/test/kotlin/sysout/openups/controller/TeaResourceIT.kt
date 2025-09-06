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
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().statusCode(201).extract().path<String>("id")
        RestAssured.given().get("/teas/$id").then().statusCode(200).body("name", equalTo("Sencha"))
    }

    @Test
    fun `deve filtrar teas por category, caffeineLevel e origin`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")
        RestAssured.given().queryParam("category", "GREEN").queryParam("caffeineLevel", "MEDIUM").queryParam("origin", "japan")
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
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        val updateJson = """
            {"name":"Sencha Atualizado","origin":"china","description":"Chá verde chinês","category":"GREEN","caffeineLevel":"LOW"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(updateJson).put("/teas/$id").then().statusCode(200).body("name", equalTo("Sencha Atualizado"))
    }

    @Test
    fun `deve deletar tea`() {
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        val id = RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas").then().extract().path<String>("id")
        RestAssured.given().delete("/teas/$id").then().statusCode(204)
        RestAssured.given().get("/teas/$id").then().statusCode(404)
    }

    @Test
    fun `deve retornar lista vazia para filtro sem resultado`() {
        // Certifique-se que o banco de dados está realmente limpo
        RestAssured.given().delete("/teas")

        // Adicione um chá que não corresponde ao filtro que usaremos
        val teaJson = """
            {"name":"Sencha","origin":"japan","description":"Chá verde","category":"GREEN","caffeineLevel":"MEDIUM"}
        """.trimIndent()
        RestAssured.given().contentType(ContentType.JSON).body(teaJson).post("/teas")

        // Verifique se há chás após a adição
        RestAssured.given().get("/teas")
            .then().statusCode(200).body("size()", equalTo(1))

        // Agora faça a consulta com um filtro que não corresponde a nenhum chá
        RestAssured.given().queryParam("category", "FLORAL").get("/teas")
            .then().statusCode(200).body("size()", equalTo(0))
    }
}

