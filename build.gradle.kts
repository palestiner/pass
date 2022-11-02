
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.palestiner"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
                jvmTarget = "17"
            }
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.github.tulskiy:jkeymaster:1.3")
                implementation("org.slf4j:slf4j-jdk14:2.0.3")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.palestiner.pass.MainKt"
        nativeDistributions {
            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi/*, TargetFormat.Deb*/)
            packageName = "pass"
            packageVersion = "1.1.0"
            description = "Passwords provider app"
            copyright = "© 2022 Palestiner. All rights reserved."
            vendor = "palestiner"
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
        }
    }
}
