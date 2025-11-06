object Dependencies {

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val material = "com.google.android.material:material:${Versions.material}"

        const val startup = "androidx.startup:startup-runtime:${Versions.startup}"

        const val camera2 = "androidx.camera:camera-camera2:${Versions.camera}"
        const val cameraVideo = "androidx.camera:camera-video:${Versions.cameraViewAndVideo}"
        const val cameraLifecycle = "androidx.camera:camera-lifecycle:${Versions.camera}"
        const val cameraView = "androidx.camera:camera-view:${Versions.cameraViewAndVideo}"
        const val cameraExtensions =
            "androidx.camera:camera-extensions:${Versions.cameraViewAndVideo}"

        const val paging = "androidx.paging:paging-runtime:${Versions.paging}"
        const val pagingCompose = "androidx.paging:paging-compose:${Versions.pagingCompose}"

        const val datastorePreferences =
            "androidx.datastore:datastore-preferences:${Versions.datastore}"

        const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    }

    object Lifecycle {
        const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
        const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
        const val composeViewModel =
            "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
    }

    object Compose {
        const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
        const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"

        const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose}"

        // Tooling support (Previews, etc.)
        const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"

        // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
        const val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose}"

        // Material Design
        const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"

        // Material design icons
        const val composeIconsCore =
            "androidx.compose.material:material-icons-core:${Versions.compose}"
        const val composeIconsExtended =
            "androidx.compose.material:material-icons-extended:${Versions.compose}"

        // Compose navigation
        const val composeNavigation =
            "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    }

    object TensorFlow {
        const val metadata =
            "org.tensorflow:tensorflow-lite-metadata:${Versions.tensorflowMetadata}"
        // GPU delegate removed due to incompatibility with tensorflow-lite-support:0.4.4
        // Using XNNPACK CPU delegate instead, which provides excellent performance on ARM64
        // const val gpu = "org.tensorflow:tensorflow-lite-gpu:${Versions.tensorflowGpu}"
        const val vision = "org.tensorflow:tensorflow-lite-task-vision:${Versions.tensorFlow}"
        const val support = "org.tensorflow:tensorflow-lite-support:${Versions.tensorFlow}"
    }

    object Hilt {
        const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltCoreVersion}"
        const val hiltNavigationCompose =
            "androidx.hilt:hilt-navigation-compose:${Versions.hiltComposeNavigation}"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltCoreVersion}"
        const val hiltAndroidxCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltVersion}"
        const val hiltAndroidTesting =
            "com.google.dagger:hilt-android-testing:${Versions.hiltCoreVersion}"
        const val hiltAndroidTestCompiler =
            "com.google.dagger:hilt-compiler:${Versions.hiltCoreVersion}"
    }

    object Coil {
        const val coil = "io.coil-kt:coil-compose:${Versions.coil}"
    }

    object Firebase {
        const val gms = "com.google.gms:google-services:4.3.10"
        const val bom = "com.google.firebase:firebase-bom:30.1.0"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val performance = "com.google.firebase:firebase-perf-ktx"
    }

    object Test {
        const val junit = "junit:junit:${Versions.junit}"
        const val extJUnit = "androidx.test.ext:junit:${Versions.extJUnit}"

        const val espressoCore =
            "androidx.test.espresso:espresso-core:${Versions.espressoCore}"

        const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoCore}"
        const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
        const val mockitoKotlin =
            "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

        const val composeUiTestJunit = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
    }

    object Ktlint {
        const val pluginName = "org.jlleitschuh.gradle.ktlint"
        const val gitHook = "plugins.git-hook"
    }

    object Utils {
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

        const val detektFormatting =
            "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}"

        // Accompanist stuff
        const val accompanistInsets =
            "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
        const val accompanistSystemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
        const val accompanistPermission =
            "com.google.accompanist:accompanist-permissions:${Versions.accompanist}"
        const val accompanistUi =
            "com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}"
    }
}