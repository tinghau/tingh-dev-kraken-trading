plugins {
    id("application")
    id("java")
}

group = "dev.tingh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    testImplementation(platform("org.junit:junit-bom:5.13.1"))
    testImplementation("org.junit.platform:junit-platform-launcher:1.13.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    applicationDefaultJvmArgs = listOf("-Xmx128m")
    mainClass = "dev.tingh.TradingApplicationManager"
}