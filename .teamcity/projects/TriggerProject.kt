package projects

import buildTypes.BuildDotNet
import jetbrains.buildServer.configs.kotlin.Project

object TriggerProject: Project({
    name = "Trigger Project"
    description = "Trigger Project"

    val buildDotNet = BuildDotNet()
    buildType(buildDotNet)
})