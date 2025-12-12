package sysout.openups.auth.service

import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration

@ApplicationScoped
class JwtTokenProvider {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    lateinit var issuer: String

    fun generateToken(username: String, roles: Set<String>): String {
        return Jwt.issuer(issuer)
            .subject(username)
            .groups(roles)
            .expiresIn(Duration.ofMinutes(30)) // 30 minutos
            .sign()
    }
}
