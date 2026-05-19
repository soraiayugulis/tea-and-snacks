plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("io.quarkus") version "3.13.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val kotlinVersion = "2.0.0"
val quarkusVersion = "3.13.1"
val mockitoKotlinVersion = "5.2.1"
val restAssuredVersion = "5.3.0"
val hamcrestVersion = "2.2"

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-smallrye-openapi") // swagger openapi
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-elytron-security-common")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    // unit test
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("io.quarkus:quarkus-junit5:$quarkusVersion")
    testImplementation("io.quarkus:quarkus-junit5-mockito:$quarkusVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    // integ test
    testImplementation("io.quarkus:quarkus-test-common:$quarkusVersion")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("org.testcontainers.docker.client.strategy", "dockerdesktop")
}
