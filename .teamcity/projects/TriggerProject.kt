package projects

import buildTypes.Dump
import jetbrains.buildServer.configs.kotlin.Project

object TriggerProject: Project({
    name = "Trigger Project"
    description = "Trigger Project"

    val dump = Dump()
    buildType(dump)
})