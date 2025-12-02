pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        SONARQUBE = 'SonarQubeLocal'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        NEXUS_REPO_URL = "http://localhost:8081/repository/mavenreleases/"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                dir('eventsProject') {
                    sh 'mvn clean test'
                    sh 'mvn clean package -DskipTests=false'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('eventsProject') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Upload .jar to Nexus') {
            steps {
                dir('eventsProject') {
                    sh '''
                    mvn deploy -DaltDeploymentRepository=releases::default::http://localhost:8081/repository/mavenreleases/
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t sarra/eventsproject:latest .'
                }
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push sarra/eventsproject:latest"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }
    }
}
