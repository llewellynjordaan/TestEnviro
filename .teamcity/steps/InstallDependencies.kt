package steps

import jetbrains.buildServer.configs.kotlin.buildSteps.PowerShellStep

/**
 * Step to install some required dependencies / tooling for our pipeline.
 */
class InstallDependencies(
    /**
     * The directory we should install SSDT to.
     */
    private val ssdtDirectory: String = "C:\\SSDT",
): PowerShellStep({
    name = "\uD83D\uDCAA Install Dependencies"
    scriptMode = script {
        content = """
            ${'$'}ErrorActionPreference = 'Stop';
    
            ${'$'}SSDT_VERSION = "10.0.61804.210";
            ${'$'}env:SSDTPath = "$ssdtDirectory\Microsoft.Data.Tools.Msbuild.${'$'}SSDT_VERSION\lib\net46";
            ${'$'}env:SQLDBExtensionsRefPath = "${'$'}env:SSDTPath";
            
            Write-Output "##teamcity[setParameter name='env.SSDTPath' value='${'$'}env:SSDTPath']";
            Write-Output "##teamcity[setParameter name='env.SQLDBExtensionsRefPath' value='${'$'}env:SQLDBExtensionsRefPath']";
            
            if (!(Test-Path ${'$'}env:SSDTPath)) {
                nuget install Microsoft.Data.Tools.Msbuild `
                    -Version "${'$'}SSDT_VERSION" `
                    -OutputDirectory "$ssdtDirectory";
                
                if (${'$'}lastexitcode -ne 0) {
                    exit 1
                }
            } else {
                Write-Output "SSDT is already installed.";
            }
            
            choco install octopustools -y
        """.trimIndent()
    }
})