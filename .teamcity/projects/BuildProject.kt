package projects

import buildTypes.BuildDotNet
import jetbrains.buildServer.configs.kotlin.Project

object BuildProject: Project({
    name = "Build Project"
    description = "Build Project"

    val buildDotNet = BuildDotNet()
    buildType(buildDotNet)
})