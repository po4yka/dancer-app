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

val preCommitHook by tasks.named("installGitHooks")

tasks.getByPath(":app:preBuild").apply {
    dependsOn += preCommitHook
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
