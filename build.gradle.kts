plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.10.2"

    implementation("commons-io:commons-io:2.15.1")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20240205")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.google.jimfs:jimfs:1.3.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
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
