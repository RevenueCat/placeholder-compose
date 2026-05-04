import com.revenuecat.placeholder.Configuration

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
    id(libs.plugins.kotlin.serialization.get().pluginId)
    id(libs.plugins.jetbrains.compose.get().pluginId)
    id(libs.plugins.compose.compiler.get().pluginId)
    id(libs.plugins.nexus.plugin.get().pluginId)
    id(libs.plugins.dokka.get().pluginId)
    id(libs.plugins.baseline.profile.get().pluginId)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
    val artifactId = "placeholder"
    coordinates(
        Configuration.artifactGroup,
        artifactId,
        rootProject.extra.get("libVersion").toString()
    )

    pom {
        name.set(artifactId)
        description.set("Fully customizable placeholder loading effects for Jetpack Compose and KMP.")
    }
}

kotlin {
    androidLibrary {
        namespace = "com.revenuecat.purchases.placeholder"
        compileSdk = Configuration.compileSdk
        minSdk = Configuration.minSdk

        withDeviceTest { }
    }
    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.compose.effects)
            }
        }
        getByName("androidDeviceTest").dependencies {
            implementation(kotlin("test"))
        }
    }

    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

tasks.withType<JavaCompile>().configureEach {
    this.targetCompatibility = libs.versions.jvmTarget.get()
    this.sourceCompatibility = libs.versions.jvmTarget.get()
}
