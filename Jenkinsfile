pipeline {
    agent any

    environment {
      MAVEN_IMAGE = 'maven:3.5-jdk-8'
    }
    
    stages {
        stage("Test") {
            steps {
              script {
                  /*docker.image(MAVEN_IMAGE).inside('-v /var/lib/jenkins/.m2:/root/.m2') {
                      sh 'mvn test'
                   }*/
                  sh 'ls'
               }
            }
        }
        stage("Deploy Dev") {
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: 'ansible/inventories/build.yml', playbook: 'ansible/playbook.yml')
            }
        }
    }
}