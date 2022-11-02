package buildTypes

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

class Credentials(): BuildType ({
    name = "Credentials"
    description = "Credentials"

    params {
        param("octopus", "credentialsJSON:181f87a5-10cd-4c10-a740-09ead7bf6b65")
    }

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
        checkoutMode = CheckoutMode.ON_AGENT
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
            """.trimIndent()
        }
    }

    steps {
        script {
            scriptContent = """
                echo %octopus%
            """.trimIndent()
        }
    }
})