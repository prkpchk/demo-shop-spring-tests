pipeline {
    agent {
        docker {
            image 'eclipse-temurin:21-jdk-jammy'
            args  '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    triggers {
        // Only run on push events (managed by webhook in Jenkins GitHub/GitLab plugin)
        pollSCM('')
    }

    environment {
        HEADLESS      = 'true'
        BROWSER       = 'chrome'
        APP_BASE_URL  = 'http://demoshop-app:8080'
        APP_UI_URL    = 'http://demoshop-frontend:80'
        DB_URL        = 'jdbc:postgresql://demoshop-postgres:5432/demoshop'
        DB_USER       = 'demoshop'
        DB_PASSWORD   = 'demoshop'
        ALLURE_DIR    = 'build/allure-results'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start demo-shop stack') {
            steps {
                sh '''
                    curl -fsSL https://get.docker.com | sh || true
                    curl -SL "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-linux-x86_64" \
                         -o /usr/local/bin/docker-compose
                    chmod +x /usr/local/bin/docker-compose
                    docker-compose -f docker-compose.hub.yml up -d
                    # Wait for the app healthcheck
                    timeout 120 bash -c \
                        "until curl -sf http://localhost:8080/actuator/health; do sleep 5; done"
                '''
            }
        }

        stage('Run Tests in Parallel') {
            parallel {
                stage('API Tests') {
                    steps {
                        sh './gradlew test -Dspring.profiles.active=ci -Dtest.tag=api --continue'
                    }
                }
                stage('UI Tests') {
                    steps {
                        sh './gradlew test -Dspring.profiles.active=ci -Dtest.tag=ui --continue'
                    }
                }
            }
        }
    }

    post {
        always {
            // Collect Allure results and generate report
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'build/allure-results']]
            ])

            // Archive screenshots and videos
            archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'build/allure-report/**/*', allowEmptyArchive: true

            // Publish JUnit results
            junit testResults: 'build/test-results/**/*.xml', allowEmptyResults: true

            sh 'docker-compose -f docker-compose.hub.yml down || true'
        }
    }
}
