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
       stage("Publish image") {
           steps {
               script {
                  sh 'mvn clean package dockerfile:build'
               }
                script {
                  docker.withRegistry('', 'dockerhub-credentials') {
                        docker.image("slonepi/reference-collector:latest").push()
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
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: '~/ansible/prod.yml', playbook: 'ansible/playbook.yml')
            }
        }
    }
}