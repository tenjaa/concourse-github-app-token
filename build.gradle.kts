plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.21.0")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.json:json:20251224")
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
