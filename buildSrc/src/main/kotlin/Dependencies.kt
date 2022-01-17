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

        const val camera2 = "androidx.camera:camera-camera2:${Versions.camera}"
        const val cameraLifecycle = "androidx.camera:camera-lifecycle:${Versions.camera}"
        const val cameraView = "androidx.camera:camera-view:${Versions.cameraView}"
    }

    object Compose {
        const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
        const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
        const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"

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
}