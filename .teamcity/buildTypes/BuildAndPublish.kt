package buildTypes

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import steps.*

import steps.CompareMasterRevision

class BuildAndPublish: BuildType({
    name = "Build and Publish"
    description = "Build all projects in the solution and publish artefacts"

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

    val artifactRulesDef = """
            +:project/TestEnviro.Database=>TestEnviro.Database.zip
            +:project/TestEnviro.Api=>TestEnviro.Api.zip
            +:project/TestEnviro.Web=>TestEnviro.Web.zip
        """.trimIndent()

    steps {
        script {
            scriptContent = "Build solution and publish artefacts"
        }

        /**
         * Small step to prevent double builds when breaking a release branch off of master
         */
        step(CompareMasterRevision())

        /**
         * Auto versioning our artifacts
         */
        step(AutoVersion())

        /**
         * Ensure we have the required dependencies / tooling for our DB.
         */
        step(InstallDependencies())

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