import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.9"
	id("io.spring.dependency-management") version "1.1.0"

	id("org.graalvm.buildtools.native") version "0.9.20"

	kotlin("jvm") version "1.9.10"
	kotlin("plugin.serialization") version "1.9.10"
	kotlin("plugin.spring") version "1.7.22"
	id("org.jetbrains.kotlin.jupyter.api") version "0.12.0-56"
}

group = "org.clickprompt"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

	implementation("javax.websocket:javax.websocket-api:1.1")
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	implementation("org.jetbrains.kotlinx:kotlin-jupyter-api:0.12.0-56")
	implementation("org.jetbrains.kotlinx:kotlin-jupyter-kernel:0.12.0-56")

	// Serverless Kotlin Language Binding
	implementation(libs.kotless.lang)
	implementation(libs.kotless.lang.local)

	// Serverless Kotlin Spring
	implementation(libs.kotless.spring.boot.lang)
	implementation(libs.kotless.spring.boot.lang.local)
	implementation(libs.kotless.spring.lang.parser)

	// Serverless Kotlin Ktor
	implementation(libs.kotless.ktor.lang)
	implementation(libs.kotless.ktor.lang.local)

	implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.4.2")

	implementation("com.h2database:h2:2.2.222")
	implementation("mysql:mysql-connector-java:8.0.32")

	// Database ORM
	implementation(libs.bundles.exposed)

	// JDBI
	implementation(libs.bundles.jdbi)

	// tips: don't add follow deps to project will cause issues
	compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation(libs.bundles.test)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
