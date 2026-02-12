plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "6.0.2"

    implementation("commons-io:commons-io:2.20.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.json:json:20251224")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.3.2")
    testImplementation("com.google.jimfs:jimfs:1.3.1")
    testImplementation("org.mockito:mockito-core:5.21.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "eu.neufeldt.concoursegithubcredentials.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}
