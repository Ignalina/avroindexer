pipeline {
  agent any
  stages {
    stage('error') {
      steps {
        sh '''export M2_HOME=/usr/share/java/maven-3 

mvn clean install
'''
      }
    }

  }
}