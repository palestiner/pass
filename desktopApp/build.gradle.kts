import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.jnativehook)

    // macOS: смена activation policy ( Dock / Cmd+Tab )
    implementation("ca.weblite:java-objc-bridge:1.1")
}

compose.desktop {
    application {
        mainClass = "com.palestiner.pass.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.palestiner.pass"
            packageVersion = "1.0.0"
        }
    }
}