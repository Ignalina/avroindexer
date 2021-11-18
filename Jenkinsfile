pipeline {
  agent any
  stages {
    stage('error') {
      steps {
        sh '''export M2_HOME=/usr/share/java/maven-3 ;
export JFROG_CLI_BUILD_NUMBER=${BUILD_NUMBER} ;
export JFROG_CLI_BUILD_NAME=${JOB_NAME} ;

rm -rf ~/.m2/repository/dk/ignalina/lab/

mvn clean install
'''
      }
    }

  }
}