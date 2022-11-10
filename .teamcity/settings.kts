import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    subProject(TriggerProject)
    subProject(BuildProject)
    subProject(TestingCreds)
}


object BuildProject : Project({
    name = "Build Project"
    description = "Build Project"

    buildType(BuildDotNet)
})

object BuildDotNet : BuildType({
    name = "Build Dot Net"
    description = "Build Dot Net"

    artifactRules = "+:src=>deps.zip"

    params {
        param("teamcity.git.fetchAllHeads", "true")
    }

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            scriptContent = "dir"
        }
        powerShell {
            name = "Compare Master Revision"
            scriptMode = script {
                content = """
                    ${'$'}buildBranch = "%teamcity.build.branch%"
                    ${'$'}masterRevision = git rev-parse refs/remotes/origin/master | Out-String
                    ${'$'}buildRevision = "%build.vcs.number%"
                    
                    Write-Output "Master Revision: " + ${'$'}masterRevision
                    Write-Output "Build Revision: " + ${'$'}buildRevision
                    
                    if (${'$'}buildBranch.Trim() -ne "refs/heads/master" -and ${'$'}masterRevision.Trim() -eq ${'$'}buildRevision.Trim()) {
                        write-output "condition matched to cancel the build"
                        write-host "##teamcity[buildStop comment='Branch Up To Date With Master - Please Run Master Build' readdToQueue='false']"
                    }
                """.trimIndent()
            }
        }
        script {
            name = "Build Dot Net Project"
            scriptContent = """dotnet build ./src/TestEnviro.sln --configuration "Release""""
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                -:<default>
            """.trimIndent()
        }
    }

    features {
        pullRequests {
            provider = github {
                filterTargetBranch = """
                    +:refs/heads/develop/*
                    +:refs/heads/hotfix/*
                    +:refs/heads/feature/*
                """.trimIndent()
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
            }
        }
    }
})


object TestingCreds : Project({
    name = "Testing Creds Project"
    description = "Testing Creds Project"

    buildType(Credentials)
})

object Credentials : BuildType({
    name = "Credentials"
    description = "Credentials"

    params {
        password("octopus", "zxxc1d7ef48a7ca25f3e9fbbcfd3cf82b772a18c43f36f521e30dbbf25e876acece")
    }

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            scriptContent = "echo %octopus%"
        }
    }

    triggers {
        vcs {
        }
    }
})


object TriggerProject : Project({
    name = "Trigger Project"
    description = "Trigger Project"

    buildType(Dump)
})

object Dump : BuildType({
    name = "Dump"
    description = "Dump"

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.MANUAL
    }

    steps {
        script {
            scriptContent = "dir deps"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${BuildDotNet.id}"
            successfulOnly = true
            branchFilter = """
                +:*
                -:pull/*
            """.trimIndent()
        }
    }

    dependencies {
        dependency(BuildDotNet) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = "+:deps.zip!**=>deps"
            }
        }
    }
})
