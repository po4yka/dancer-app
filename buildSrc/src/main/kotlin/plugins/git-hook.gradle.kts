package plugins

import Dependencies.Ktlint

tasks {
    register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from scripts/git-hooks to the .git folder."
        group = Ktlint.gitHook
        from("$rootDir/scripts") {
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
    }

    register<Exec>("installGitHooks") {
        description = "Installs the pre-commit git hooks from scripts/git-hooks."
        group = Ktlint.gitHook
        workingDir(rootDir)
        commandLine("chmod")
        args("-R", "+x", ".git/hooks/")
        dependsOn(named("copyGitHooks"))
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