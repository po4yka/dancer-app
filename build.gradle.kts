import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Dependencies.Ktlint.pluginName).version(Versions.JLLeitschuhKtlintGradle)
    id(Dependencies.Ktlint.gitHook)
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(GradleConfig.android)
        classpath(GradleConfig.kotlin)
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltGradlePlugin}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.JLLeitschuhKtlintGradle}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = Dependencies.Ktlint.pluginName)
    ktlint {
        android.set(true)
        outputColorName.set("RED")
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

val preCommitHook by tasks.named("installGitHooks")

tasks.getByPath(":app:preBuild").apply {
    dependsOn += preCommitHook
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apply {
        freeCompilerArgs = freeCompilerArgs +
            "-opt-in=kotlin.time.ExperimentalTime" +
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi" +
            "-opt-in=kotlinx.coroutines.FlowPreview"
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
