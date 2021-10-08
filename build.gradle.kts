plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.8.1"

    implementation("commons-io:commons-io:2.11.0")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("org.json:json:20210307")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")
    testImplementation("com.google.jimfs:jimfs:1.2")
    testImplementation("org.mockito:mockito-core:4.0.0")
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
    sourceCompatibility = JavaVersion.VERSION_11
}
