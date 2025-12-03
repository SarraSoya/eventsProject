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
                // Build du projet + tests unitaires
                sh 'mvn clean test'
                sh 'mvn clean package -DskipTests=false'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Upload .jar to Nexus') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'nexus-creds',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )
                ]) {
                    sh '''
                    mvn deploy -DaltDeploymentRepository=releases::default::http://${NEXUS_USER}:${NEXUS_PASS}@localhost:8081/repository/mavenreleases/
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Construction de l'image Docker à partir du Dockerfile
                    sh 'docker build -t sarrasoyah/eventsproject:latest .'
                }
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                // Connexion à DockerHub puis push de l'image
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push sarrasoyah/eventsproject:latest"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                // Utilisation de Docker Compose V2 (docker compose, pas docker-compose)
                sh '''
                    docker compose down || true
                    docker compose up -d
                '''
            }
        }
    }
}

