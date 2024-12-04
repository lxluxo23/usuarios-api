pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Cleanup') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker builder prune -f'
            }
        }

        stage('Build and Deploy') {
            steps {
                sh 'docker-compose build --no-cache'
                sh 'docker-compose up -d'
            }
        }

        stage('Health Check') {
            steps {
                script {
                    sh 'sleep 30'
                    sh 'docker-compose ps'
                }
            }
        }
    }

    post {
        failure {
            sh 'docker-compose logs'
        }
    }
}