import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.8"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.vaadin") version "24.1.2"
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.spring") version "1.7.10"
    kotlin("plugin.jpa") version "1.7.10"
}

group = "ru.sfedu.geo"
version = "0.0.1-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val vaadinVersion = "24.1.0"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.vaadin.com/vaadin-addons") }
    maven { url = uri("https://maven.vaadin.com/vaadin-prereleases") }
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:$vaadinVersion")
    }
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("com.jayway.jsonpath:json-path:2.8.0")

    // vaadin
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("org.vaadin.addons.componentfactory:vcf-pdf-viewer:2.7.2")
    implementation("com.flowingcode.vaadin.addons:google-maps:1.10.1")
    implementation("com.graphhopper:jsprit-core:1.7.2")

    // jasper
    // implementation("net.sf.jasperreports:jasperreports:6.20.0")
    // implementation("net.sf.jasperreports:jasperreports-fonts:6.20.0")

    // data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage:junit-vintage-engine")
    }
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.springframework.boot:spring-boot-starter-tomcat")
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

tasks {
    jar {
        enabled = false
    }

    bootJar {
        archiveVersion.set("")
    }
}

vaadin {
    productionMode = true
}
