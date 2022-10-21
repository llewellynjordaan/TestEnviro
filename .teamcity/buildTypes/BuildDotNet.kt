package buildTypes

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import steps.CompareMasterRevision

class BuildDotNet: BuildType({
    name = "Build Dot Net"
    description = "Build Dot Net"

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
        checkoutMode = CheckoutMode.ON_AGENT
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                -:<default>
            """.trimIndent()
        }
    }

    steps {
        step(CompareMasterRevision())

        script {
            name = "Build Dot Net Project"
            scriptContent = """
                dotnet build ./src/TestEnviro.sln --configuration "Release"
            """.trimIndent()
        }
    }

    features {
        pullRequests {
            provider = github {
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                filterTargetBranch = """
                    +:refs/heads/develop/*
                    +:refs/heads/hotfix/*
                    +:refs/heads/feature/*
                """.trimIndent()
            }
        }
    }
})