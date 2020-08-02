plugins {
    java
    application
}
apply("dependencies.gradle")

repositories {
    jcenter()
    mavenCentral()
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
