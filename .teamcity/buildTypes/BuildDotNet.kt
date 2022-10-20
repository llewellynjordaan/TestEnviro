package buildTypes

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

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
                +:refs/heads/release/*
                +:refs/heads/minor-release/*
            """.trimIndent()
        }
    }

    steps {
        script {
            name = "Build Dot Net Project"
            scriptContent = """
                dotnet build ./src/TestEnviro.sln --configuration "Release"
            """.trimIndent()
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = DslContext.settingsRoot.id.toString()
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:95cbd4b7-e264-4fee-b9e6-3ac94341ab56"
                }
            }
        }
        pullRequests {
            provider = github {
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
                filterTargetBranch = """
                    +:refs/heads/develop/brumbies
                    +:refs/heads/hotfix/*
                    +:refs/heads/feature/*
                """.trimIndent()
            }
        }
    }
})