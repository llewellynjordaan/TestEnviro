package projects

import jetbrains.buildServer.configs.kotlin.Project
import buildTypes.BuildAndPublish

object PullRequests: Project({
    name = "Pull Requests"
    description = "Pull Requests"

    params {
        param("component.uuid", "123456")
        param("component.directory", ".")
        param("deployment.version.unique", "%build.number%")
        param("root.solution.directory", "./project")
    }

    val buildSolution = BuildAndPublish()
    buildType(buildSolution)
})