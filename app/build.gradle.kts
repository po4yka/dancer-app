import Dependencies.AndroidX
import Dependencies.Compose
import Dependencies.Kotlin
import Dependencies.Test
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.github.ben-manes.versions").version(Versions.gradleVersionsPlugin)
    id("org.jlleitschuh.gradle.ktlint").version(Versions.JLLeitschuhKtlintGradle)
}

android {
    compileSdk = Config.compileSdkVersion
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        // FIXME: temporary solution, see: https://stackoverflow.com/a/69067455/10498948
        configurations.all {
            resolutionStrategy { force("androidx.core:core-ktx:1.6.0") }
        }

        applicationId = Config.applicationId
        minSdk = Config.minSdkVersion
        targetSdk = Config.targetSdkVersion
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = Config.testInstrumentationRunner
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = Config.jvmTarget
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.Material)
    implementation(AndroidX.constraintLayout)

    implementation(Kotlin.stdlib)
    implementation(Kotlin.coroutines)

    implementation(Compose.composeUi)
    implementation(Compose.composeUiTooling)
    implementation(Compose.composeFoundation)
    implementation(Compose.composeMaterial)
    implementation(Compose.composeIconsCore)
    implementation(Compose.composeIconsExtended)

    testImplementation(Test.junit)
    testImplementation(Test.extJUnit)

    androidTestImplementation(Test.espressoCore)

    androidTestImplementation(Test.mockitoCore)
    androidTestImplementation(Test.mockitoInline)
    androidTestImplementation(Test.mockitoKotlin)
    androidTestImplementation(Test.composeUiTestJunit)
}

/* Gradle Versions Plugin Setup */

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

// Reference: https://github.com/ben-manes/gradle-versions-plugin#kotlin-dsl
tasks.withType<DependencyUpdatesTask> {

    // reject all non stable versions
    rejectVersionIf {
        candidate.version.isNonStable()
    }

    // disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }

    // using the full syntax
    resolutionStrategy {
        componentSelection {
            all {
                if (candidate.version.isNonStable() && !currentVersion.isNonStable()) {
                    reject("Release candidate")
                }
            }
        }
    }

    // optional parameters
    checkForGradleUpdate = true
}
