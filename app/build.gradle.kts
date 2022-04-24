import Dependencies.AndroidX
import Dependencies.Compose
import Dependencies.Firebase
import Dependencies.Hilt
import Dependencies.Kotlin
import Dependencies.Lifecycle
import Dependencies.TensorFlow
import Dependencies.Test
import Dependencies.Utils
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import java.nio.file.Paths
import java.util.Properties

plugins {
    id("kotlin-android")
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.github.ben-manes.versions").version(Versions.gradleVersionsPlugin)
    id("io.gitlab.arturbosch.detekt").version(Versions.detekt)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.firebase-perf")

    kotlin("kapt")
}

android {
    signingConfigs {
        create("release") {
            val properties = File(
                rootDir,
                Paths.get("/", "app", "signing.properties").toString()
            ).inputStream().use {
                Properties().apply { load(it) }
            }
            storeFile = File(properties.getProperty("storeFilePath") ?: return@create)
            storePassword = properties.getProperty("storePassword") ?: return@create
            keyPassword = properties.getProperty("keyPassword") ?: return@create
            keyAlias = properties.getProperty("keyAlias") ?: return@create
        }
    }
    compileSdk = Config.compileSdkVersion
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {

        applicationId = Config.applicationId
        minSdk = Config.minSdkVersion
        targetSdk = Config.targetSdkVersion
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = Config.testInstrumentationRunner
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = Config.jvmTarget
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        mlModelBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    androidResources {
        noCompress.add("tflite")
    }
}

dependencies {
    implementation(project(":gallerypicker"))

    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.material)
    implementation(AndroidX.startup)
    implementation(AndroidX.constraintLayout)

    implementation(Kotlin.stdlib)
    implementation(Kotlin.coroutines)

    implementation(Compose.composeUi)
    implementation(Compose.composeRuntime)
    implementation(Compose.composeUiTooling)
    implementation(Compose.composeFoundation)
    implementation(Compose.composeMaterial)
    implementation(Compose.composeIconsCore)
    implementation(Compose.composeNavigation)
    implementation(Compose.composeIconsExtended)

    implementation(Hilt.hiltAndroid)
    implementation(Hilt.hiltNavigationCompose)
    kapt(Hilt.hiltCompiler)
    kapt(Hilt.hiltAndroidxCompiler)
    androidTestImplementation(Hilt.hiltAndroidTesting)
    kaptAndroidTest(Hilt.hiltAndroidTestCompiler)

    implementation(Lifecycle.composeActivity)
    implementation(Lifecycle.composeViewModel)
    implementation(Lifecycle.lifecycleKtx)

    implementation(AndroidX.camera2)
    implementation(AndroidX.cameraVideo)
    implementation(AndroidX.cameraLifecycle)
    implementation(AndroidX.cameraView)
    implementation(AndroidX.cameraExtensions)

    implementation(TensorFlow.metadata)
    implementation(TensorFlow.gpu)
    implementation(TensorFlow.vision)
    implementation(TensorFlow.support)

    implementation(Utils.accompanistInsets)
    implementation(Utils.accompanistSystemUiController)
    implementation(Utils.accompanistPermission)
    implementation(Utils.accompanistUi)
    implementation(Utils.timber)

    implementation(platform(Firebase.bom))
    implementation(Firebase.analytics)
    implementation(Firebase.performance)

    detektPlugins(Utils.detektFormatting)

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
    val stableKeyword = listOf("RELEASE", "FINAL", "GA", "RC").any { toUpperCase().contains(it) }
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

/* Detekt */

// Reference: https://github.com/detekt/detekt#with-gradle
detekt {
    config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) /* similar to the console output,
                                  contains issue signature to manually edit baseline files */
        sarif.required.set(true) /* standardized SARIF format (https://sarifweb.azurewebsites.net/)
                                    to support integrations with Github Code Scanning */
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = Config.jvmTarget
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = Config.jvmTarget
}
