pipeline {
  agent any
  stages {
    stage('error') {
      steps {
        sh '''export M2_HOME=/usr/share/java/maven-3 ;

rm -rf ~/.m2/repository/dk/ignalina/lab/

jfrog rt mvn clean install
'''
      }
    }

  }
  environment {
    JFROG_CLI_BUILD_NAME = '${env.JOB_NAME}'
    JFROG_CLI_BUILD_NUMBER = '${env.BUILD_NUMBER}'
  }
}