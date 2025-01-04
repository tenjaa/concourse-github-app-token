plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.11.4"

    implementation("commons-io:commons-io:2.18.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.json:json:20241224")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.google.jimfs:jimfs:1.3.0")
    testImplementation("org.mockito:mockito-core:5.15.2")
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
