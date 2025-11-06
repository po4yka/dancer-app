plugins {
    id("kotlin-android")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose") version Versions.kotlin
}

android {
    namespace = "com.po4yka.gallerypicker"

    compileSdk = Config.compileSdkVersion
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        minSdk = Config.minSdkVersion

        testInstrumentationRunner = Config.testInstrumentationRunner
    }

    lint {
        targetSdk = Config.targetSdkVersion
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xjvm-default=all-compatibility")
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(Dependencies.Compose.composeUi)
    implementation(Dependencies.Compose.composeRuntime)
    implementation(Dependencies.Compose.composeUiTooling)
    implementation(Dependencies.Compose.composeFoundation)
    implementation(Dependencies.Compose.composeMaterial)

    implementation(Dependencies.Lifecycle.composeActivity)
    implementation(Dependencies.Lifecycle.composeViewModel)
    implementation(Dependencies.Lifecycle.lifecycleKtx)

    implementation(Dependencies.AndroidX.paging)
    implementation(Dependencies.AndroidX.pagingCompose)

    implementation(Dependencies.Coil.coil)
    implementation(Dependencies.Utils.accompanistPermission)

    debugApi(Dependencies.Compose.composeUiTooling)
}
