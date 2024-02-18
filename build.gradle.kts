plugins {
    kotlin("jvm") version "1.9.21"
    id("groovy")
}

val kotlinVersion = "1.9.21"

group = "com.momid.log"
version = "1.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:$kotlinVersion")
    implementation("org.codehaus.groovy:groovy-all:3.0.12")
    implementation("net.objecthunter:exp4j:0.4.8")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}