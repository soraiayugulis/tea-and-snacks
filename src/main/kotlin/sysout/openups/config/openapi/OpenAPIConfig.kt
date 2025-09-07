package sysout.openups.config.openapi

import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Application
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@ApplicationScoped
@Unremovable
@OpenAPIDefinition(
    info = Info(
        title = "Tea 'n Snacks API",
        version = "1.0.0"
    ),
    tags = [
        Tag(name = "Teas", description = "Tea operations"),
        Tag(name = "Snacks", description = "Snack operations"),
        Tag(name = "Sauces", description = "Sauce operations")
    ]
)
class OpenAPIConfig : Application() {
    // A ordem das tags definidas acima determina a ordem de exibição no Swagger UI
}