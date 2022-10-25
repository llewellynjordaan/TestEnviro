package steps

import jetbrains.buildServer.configs.kotlin.buildSteps.PowerShellStep

class CompareMasterRevision: PowerShellStep ({
    name = "Compare Master Revision"
    scriptMode = script {
        content = """
            ${'$'}buildBranch = "%teamcity.build.branch%"
            ${'$'}masterRevision = git rev-parse refs/heads/master | Out-String
            ${'$'}buildRevision = "%build.vcs.number%"
            
            Write-Output "Master Revision: " + ${'$'}masterRevision
            Write-Output "Build Revision: " + ${'$'}buildRevision
            
            if (${'$'}buildBranch.Trim() -ne "refs/heads/master" -and ${'$'}masterRevision.Trim() -eq ${'$'}buildRevision.Trim()) {
                write-output "condition matched to cancel the build"
                write-host "##teamcity[buildStop comment='Branch Up To Date With Master - Please Run Master Build' readdToQueue='false']"
            }
        """.trimIndent()
    }
})