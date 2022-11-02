package projects

import buildTypes.Credentials
import jetbrains.buildServer.configs.kotlin.Project

object TestingCreds: Project({
    name = "Testing Creds Project"
    description = "Testing Creds Project"

    val testCredentials = Credentials()
    buildType(testCredentials)
})