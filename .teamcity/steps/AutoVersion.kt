package steps

import jetbrains.buildServer.configs.kotlin.buildSteps.PowerShellStep

class AutoVersion(): PowerShellStep({
    name = "AutoVersion"
    scriptMode = script {
        content = """
                    ${'$'}branchname = git rev-parse --abbrev-ref HEAD
                    ${'$'}branchtag = git describe --abbrev=0 --tags
                    ${'$'}version_file = "./build/version.template.txt"
                    ${'$'}global:artifact_version = ${'$'}null
                    
                    write-host "%##teamcity[setParameter name='build_branch_name' value='${'$'}(${'$'}branchname)']%" 
                    
                    function set_artifact_version(${'$'}input_version){
                    	${'$'}global:artifact_version = ${'$'}input_version
                        write-host "%##teamcity[setParameter name='global_artifact_version' value='${'$'}(${'$'}global:artifact_version)']%" 
                    	${'$'}global:artifact_version | Set-Content ${'$'}version_file #-encoding ASCII
                    }
                    
                    switch -wildcard (${'$'}branchname) {
                        "master" { set_artifact_version(${'$'}branchtag) }
                        "HEAD" { set_artifact_version(${'$'}branchtag) }
                        "feature/*" { set_artifact_version(${'$'}branchtag) }
                        "develop/*" { set_artifact_version(${'$'}branchtag) }
                        "release/*" { set_artifact_version(${'$'}branchname -replace "release/") }
                    	"minor-release/*" { set_artifact_version(${'$'}branchname -replace "minor-release/") }
                        "default" { 
                            write-host "Failure: unknown branch ${'$'}(${'$'}branchname)" 
                            exit 1
                        }
                    }
                    
                    write-host 'object ${'$'}branchname:' ${'$'}branchname
                    write-host 'object ${'$'}branchtag:' ${'$'}branchtag
                    write-host 'object ${'$'}global:artifact_version' ${'$'}global:artifact_version
                """.trimIndent()
    }
    param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
    param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
    param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
})