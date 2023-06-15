plugins {
    id("java")
}

group = "org.trackandtrace"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.hyperledger.fabric-chaincode-java:fabric-chaincode-shim:2.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")

    runtimeOnly("io.grpc:grpc-netty-shaded:1.55.1")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}