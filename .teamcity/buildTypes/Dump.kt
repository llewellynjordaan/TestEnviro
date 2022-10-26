package buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger

class Dump: BuildType({
    name = "Dump"
    description = "Dump"

    val buildDependency = "TestEnviro_BuildDotNet"

    vcs {
        root(DslContext.settingsRoot)
        checkoutMode = CheckoutMode.MANUAL
    }

    triggers {
        finishBuildTrigger {
            buildType = buildDependency
            successfulOnly = true
            branchFilter = "+:*"
        }
    }

    steps {
        script {
            scriptContent = "dir deps"
        }
    }

    dependencies {
        dependency(AbsoluteId(buildDependency)) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = """
                    +:deps.zip!**=>deps
                """.trimIndent()
            }
        }
    }
})