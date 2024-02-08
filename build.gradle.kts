import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.7"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.avast.gradle.docker-compose") version "0.17.6"
    id("org.jmailen.kotlinter") version "3.14.0"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
}

group = "io.mustelidae"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
}

ext["log4j2.version"] = "2.17.1"

dependencies {
    implementation(kotlin("stdlib:1.9.21"))
    implementation(kotlin("reflect:1.9.21"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1") // library deprecated.

    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.9.0")

    testImplementation("io.mockk:mockk:1.9.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude("io.undertow", "undertow-websockets-jsr")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-hateoas:3.1.7")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1.4")
    implementation("io.swagger.parser.v3:swagger-parser-v2-converter:2.1.20")
    // Java Version
    implementation("javax.xml.bind:jaxb-api:2.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform()

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                val output =
                    "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n${"-".repeat(repeatLength)}\n|  $output  |\n${"-".repeat(repeatLength)}")
            }
        }
    })
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.register("version") {
    println(version)
}

dockerCompose {
// docker-compose
    // settings as usual
    createNested("infraSetting").apply {
        stopContainers = false
        useComposeFiles = listOf("docker-compose.yml")
    }
}
