// Reference: https://blog.sebastiano.dev/ooga-chaka-git-hooks-to-enforce-code-quality/
package plugins

import Dependencies.Ktlint

fun isLinuxOrMacOs(): Boolean {
    val osName = System.getProperty("os.name").toLowerCase()
    return osName.contains("linux") || osName.contains("mac os") || osName.contains("macos")
}

tasks {
    register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from scripts/git-hooks to the .git folder."
        group = Ktlint.gitHook
        from("$rootDir/git-hooks") {
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
        onlyIf { isLinuxOrMacOs() }
        doLast {
            logger.info("Git hook copied successfully.")
        }
    }

    register<Exec>("installGitHooks") {
        description = "Installs the pre-commit git hooks from scripts/git-hooks."
        group = Ktlint.gitHook
        workingDir(rootDir)
        commandLine("chmod")
        args("-R", "+x", ".git/hooks/")
        dependsOn(named("copyGitHooks")).onlyIf { isLinuxOrMacOs() }
        doLast {
            logger.info("Git hooks installed successfully.")
        }
    }

    register<Delete>("deleteGitHooks") {
        description = "Delete the pre-commit git hooks."
        group = Ktlint.gitHook
        delete(fileTree(".git/hooks/"))
    }

    afterEvaluate {
        tasks["clean"].dependsOn(tasks.named("installGitHooks"))
    }
}