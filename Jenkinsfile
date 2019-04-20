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
            when { branch 'develop' }
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: '~/build.yml', playbook: 'ansible/playbook.yml')
            }
        }
        stage("Deploy Prod") {
            when { branch 'master' }
            steps {
              ansiblePlaybook(credentialsId: 'ssh_centos', inventory: '~/prod.yml', playbook: 'ansible/playbook.yml')
            }
        }
    }
}