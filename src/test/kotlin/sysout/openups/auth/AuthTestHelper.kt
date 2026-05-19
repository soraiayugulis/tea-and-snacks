package sysout.openups.auth

import io.restassured.RestAssured
import io.restassured.http.ContentType

/**
 * Test helper for authentication operations in integration tests.
 */
object AuthTestHelper {

    /**
     * Login and return JWT token for the given credentials.
     */
    fun loginAndGetToken(username: String, password: String): String {
        val loginJson = """
            {"username":"$username","password":"$password"}
        """.trimIndent()

        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(loginJson)
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("token")
    }

    /**
     * Get admin token (uses seeded admin user).
     */
    fun getAdminToken(): String = loginAndGetToken("admin", "admin123")

    /**
     * Get manager token (uses seeded manager user).
     */
    fun getManagerToken(): String = loginAndGetToken("manager", "manager123")

    /**
     * Get regular user token (uses seeded user).
     */
    fun getUserToken(): String = loginAndGetToken("user", "user123")

    /**
     * Build Authorization header with Bearer token.
     */
    fun buildAuthHeader(token: String): String = "Bearer $token"
}
