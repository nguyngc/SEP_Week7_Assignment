pipeline {
    agent any
    tools{
        maven 'Maven 3.9.12'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'DockerID'
        DOCKERHUB_REPO = 'nguyngc/calculator_fx'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/nguyngc/sep_week7_assignment.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean package -DskipTests'
                    } else {
                        bat 'mvn clean package -DskipTests'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            environment {
                PATH = "/usr/local/bin:/opt/homebrew/bin:${env.PATH}"
            }
            steps {
                script {
                    if (isUnix()) {
                        sh "docker build --platform linux/amd64 -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} ."
                    } else {
                        bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    }
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            environment {
                PATH = "/usr/local/bin:/opt/homebrew/bin:${env.PATH}"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh '''
                        /usr/local/bin/docker login -u "$DH_USER" -p "$DH_PASS"
                        /usr/local/bin/docker push ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
                    '''
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, '**/target/surefire-reports/*.xml'
        }
    }
}

