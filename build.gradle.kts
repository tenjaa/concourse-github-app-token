plugins {
    java
    application
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.7")
    implementation("com.auth0:java-jwt:3.10.3")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.json:json:20200518")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.8.0")
    testImplementation("com.google.jimfs:jimfs:1.1")
    testImplementation("org.mockito:mockito-core:3.4.4")
}

application {
    mainClassName = "eu.neufeldt.concoursegithubcredentials.Main"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to application.mainClassName
        )
    }
    from(configurations
            .runtimeClasspath
            .get()
            .files
            .map { if (it.isDirectory) it else zipTree(it) })
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}
