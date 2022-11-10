package buildTypes

import jetbrains.buildServer.configs.kotlin.*
//import com.xero.teamcityhelpers.buildstep.dotnet.sonarqube.SonarqubeDotCoverSettings
//import com.xero.teamcityhelpers.buildstep.dotnet.sonarqube.runDotnetFrameworkSonarqube
//import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
//import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
//import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
//import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
//import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
//import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
//import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import steps.*

/**
 * A build configuration to build, test and run a sonarqube analysis for .NET framework projects
 */
class AnalysisBuildFramework(
    private val solutionFile: String,
    private val projectName: String,
    private val testAssemblies: String,
    private val vcsRoot: VcsRoot,
    private val artifactRulesDef: String
): BuildType ({
    name = "Build & Analysis"
    description = "Runs Sonarqube analysis on .NET Framework projects"

    requirements {
        contains("teamcity.agent.jvm.os.name", "Windows Server")
    }

    params {
        param("teamcity.git.fetchAllHeads", "true")
        param("global_artifact_version", "")
        param("build_branch_name", "")
        param("xero.nuget.gallery", """
            https://artifactory.xero-support.com/api/nuget/nuget-gallery
            https://artifactory.xero-support.com/api/nuget/nuget
        """.trimIndent())
    }

    vcs {
        root(vcsRoot)
        cleanCheckout = true
        checkoutMode = CheckoutMode.ON_AGENT
    }

    //triggers {
    //    vcs {
    //        branchFilter = """
    //            +:*
    //            -:<default>
    //        """.trimIndent()
    //    }
    //}

    artifactRules = artifactRulesDef

    steps {
        /**
         * Small step to prevent double builds when breaking a release branch off of master
         */
        step(CompareMasterRevision())

        /**
         * Auto versioning our artifacts
         */
        //step(AutoVersion())

        /**
         * Ensure we have the required dependencies / tooling for our pipeline.
         * TODO: Is this only required for Database?
         */
        //step(InstallDependencies())

        /**
         * We need to remove the package-lock.json in order to get dep check to work
         */
        //powerShell {
        //    scriptMode = script {
        //        content = "Remove-Item %root.solution.directory%/package-lock.json"
        //    }
        //}

        /**
         * Restore any required Nuget dependencies.
         */
        //step(InstallNuGetPackages(solutionFile))

        /**
         * Build and Test within our Sonarqube step
         */
        //runDotnetFrameworkSonarqube( {
        //    sonarqubeProjectKey = projectName.toId()
        //    sonarqubeProjectName = projectName
        //    dotCoverSettings = SonarqubeDotCoverSettings()
        //}) {
        //    step(BuildFramework(solutionFile))
        //    step(VsTestFramework(testAssemblies))
        //}
    }

    /**
     * Fail the build if the branch test coverage has decreased by any amount since the last build.
     */
    //failureConditions {
    //    failOnMetricChange {
    //        metric = BuildFailureOnMetric.MetricType.COVERAGE_BRANCH_PERCENTAGE
    //        threshold = 0
    //        units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
    //        comparison = BuildFailureOnMetric.MetricComparison.LESS
    //        compareTo = build {
    //            buildRule = lastSuccessful()
    //        }
    //    }
    //}

    /**
     * Set up commit status publisher to get pipeline status on our Pull Requests.
     */
    //features {
    //    feature {
    //        type = "JetBrains.SonarQube.BranchesAndPullRequests.Support"
    //        param("provider", "GitHub")
    //    }
    //    commitStatusPublisher {
    //        vcsRootExtId = vcsRoot.id.toString()
    //        publisher = github {
    //            githubUrl = "https://github.dev.xero.com/api/v3"
    //            authType = personalToken {
    //                token = "credentialsJSON:6cafbebc-1f52-448a-b1eb-e79c70326951"
    //            }
    //        }
    //    }
    //    pullRequests {
    //        provider = github {
    //            filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
    //            filterTargetBranch = """
    //                +:refs/heads/develop/*
    //                +:refs/heads/hotfix/*
    //                +:refs/heads/feature/*
    //            """.trimIndent()
    //        }
    //    }
    //}
})