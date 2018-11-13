node {
   def mvnHome
   stage('Preparation') {
      // Get code from a GitHub repository

      git 'git@gitlab.chisw.us:anton.chertash/ledoc.git'
      // Get the Maven tool.
   }
   stage('Build') {
      // Run the maven build
      sh 'mvn clean install'
   }
   stage('Results') {
      archiveArtifacts 'target/*.jar'
   }
}