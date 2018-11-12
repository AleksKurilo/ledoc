node {
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      //git ls-remote -h 'git@gitlab.chisw.us:anton.chertash/ledoc.git' HEAD

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