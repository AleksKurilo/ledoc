node {
   def mvnHome
   stage('Preparation') {
      // Get code from a GitHub repository
      git 'git@gitlab.chisw.us:anton.chertash/ledoc.git'
   }
   stage('Build') {
      // Run the maven build
      sh 'mvn clean install'
   }
   stage('Results') {
      archiveArtifacts 'target/*.jar'
   }
}

node {
    stage('Run') {
        sshPublisher(publishers: [sshPublisherDesc(configName: 'Ledoc-Develop', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'sudo /bin/systemctl start ledoc', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])    }
}