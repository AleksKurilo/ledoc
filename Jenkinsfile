pipeline {
    stages {
       stage('Preparation') {
          // Get code from a GitHub repository
          echo env.BRANCH_NAME
          git 'git@gitlab.chisw.us:anton.chertash/ledoc.git'
          echo env.BRANCH_NAME
       }
       stage('Build') {
          // Run the maven build
          sh 'mvn clean install'
       }
       stage('Results') {
          archiveArtifacts 'target/*.jar'
       }
       stage('Run') {
          sshPublisher(publishers: [sshPublisherDesc(configName: 'Ledoc-Develop', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'sudo /bin/systemctl restart ledoc', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
       }
    }
}
    