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

        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(loginJson)
            .post("/auth/login")
            .then()
            .extract()

        val statusCode = response.statusCode()
        if (statusCode != 200) {
            val body = response.body().asString()
            throw AssertionError("Login failed with status $statusCode: $body")
        }

        return response.path("token")
    }

    /**
     * Get admin token (uses seeded admin user, registers if not exists).
     */
    fun getAdminToken(): String {
        return try {
            loginAndGetToken("admin", "admin123")
        } catch (e: AssertionError) {
            // User might not exist, try to register
            registerAndGetToken("admin", "admin123", "admin@maddoxbar.com", setOf("ADMIN", "USER"))
        }
    }

    /**
     * Get manager token (uses seeded manager user, registers if not exists).
     */
    fun getManagerToken(): String {
        return try {
            loginAndGetToken("manager", "manager123")
        } catch (e: AssertionError) {
            registerAndGetToken("manager", "manager123", "manager@maddoxbar.com", setOf("MANAGER", "USER"))
        }
    }

    /**
     * Get regular user token (uses seeded user, registers if not exists).
     */
    fun getUserToken(): String {
        return try {
            loginAndGetToken("user", "user123")
        } catch (e: AssertionError) {
            registerAndGetToken("user", "user123", "user@maddoxbar.com", setOf("USER"))
        }
    }

    /**
     * Register a new user and return JWT token.
     */
    private fun registerAndGetToken(username: String, password: String, email: String, roles: Set<String>): String {
        val registerJson = """
            {"username":"$username","password":"$password","email":"$email"}
        """.trimIndent()

        // Register user
        val registerResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(registerJson)
            .post("/auth/register")
            .then()
            .extract()

        // If registration failed because user exists, try login again
        if (registerResponse.statusCode() != 201) {
            return loginAndGetToken(username, password)
        }

        // Update roles if needed (requires admin)
        if (roles.size > 1 || !roles.contains("USER")) {
            val adminToken = loginAndGetToken("admin", "admin123")
            val rolesJson = """{"roles":${roles.map { """"$it""" }.joinToString("[", ",", "]")}}"""

            RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", buildAuthHeader(adminToken))
                .body(rolesJson)
                .put("/auth/users/$username/roles")
                .then()
                .statusCode(200)
        }

        return loginAndGetToken(username, password)
    }

    /**
     * Build Authorization header with Bearer token.
     */
    fun buildAuthHeader(token: String): String = "Bearer $token"
}
