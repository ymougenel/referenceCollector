pipeline {
    agent any

    environment {
      MAVEN_IMAGE = 'maven:3.5-jdk-8'
    }
    
    stages {
        stage("Test") {
            steps {
              script {
                  docker.image(MAVEN_IMAGE).inside('-v /var/lib/jenkins/.m2:/root/.m2') {
                      sh 'mvn test'
                  }
               }
            }
        }
        stage("Deploy Dev") {
            when {
                environment name: 'env', value: 'dev'
             }
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: '~/ansible/build.yml', playbook: 'ansible/playbook.yml')
            }
        }
        stage("Deploy Prod") {
            when {
                environment name: 'env', value: 'prod'
              }
             }
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: '~/ansible/prod.yml', playbook: 'ansible/playbook.yml')
            }
        }
    }
}