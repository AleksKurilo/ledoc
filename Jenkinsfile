pipeline {
    agent any
    
    stages {
       stage('Preparation') {
           steps {
                // Get code from a GitHub repository
                echo "Branch is " + env.BRANCH_NAME
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
                archiveArtifacts 'ledoc-api/target/*.jar'
           }
       }
       stage('Run') {
           steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'Ledoc-Develop', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'sudo /bin/systemctl restart ledoc', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: 'ledoc-api/target', sourceFiles: 'ledoc-api/target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
       
           }
       }
    }
}
    