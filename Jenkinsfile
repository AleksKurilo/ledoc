pipeline {
    agent any
    
    stages {
       stage('Preparation') {
           steps {
                // Get code from a GitHub repository
                echo env.BRANCH_NAME
           }
       }
       stage('Build') {
           steps {
                // Run the maven build
                sh 'mvn clean install'
           }
       }
       stage('Results') {
           steps {
                archiveArtifacts 'target/*.jar'
           }
       }
       stage('Run') {
           steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'Ledoc-Develop', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'sudo /bin/systemctl restart ledoc', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
       
           }
       }
    }
}
    