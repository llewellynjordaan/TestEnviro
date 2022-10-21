package steps

import jetbrains.buildServer.configs.kotlin.buildSteps.PowerShellStep

class CompareMasterRevision: PowerShellStep ({
    name = "Compare Master Revision"
    scriptMode = script {
        content = """
            ${'$'}buildBranch = "%teamcity.build.branch%"
            ${'$'}masterRevision = git rev-parse HEAD | Out-String
            ${'$'}buildRevision = "%build.vcs.number%"
            
            if (${'$'}buildBranch -ne "refs/heads/master" -and ${'$'}masterRevision -eq ${'$'}buildRevision) {
                write-host "##teamcity[buildStop comment='Branch Up To Date With Master' readdToQueue='false']"
            }
        """.trimIndent()
    }
})