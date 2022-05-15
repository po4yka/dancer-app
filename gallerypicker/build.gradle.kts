plugins {
    id("kotlin-android")
    id("com.android.library")
}

android {
    compileSdk = Config.compileSdkVersion
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        minSdk = Config.minSdkVersion
        targetSdk = Config.targetSdkVersion

        testInstrumentationRunner = Config.testInstrumentationRunner
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
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
        freeCompilerArgs = listOf("-Xjvm-default=all-compatibility")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
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
