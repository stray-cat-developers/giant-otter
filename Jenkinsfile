def send_msg(msg) {
    // FIXME: customize send tool
    println msg
}

pipeline {
    agent any
    options { timestamps () }

    stages {
        stage('Lint') {
            steps {
                script {
                    sh './gradlew clean lintKotlin'
                }
            }
            post {
                failure {
                    send_msg("[$JOB_NAME] Lint fail.")
                }
            }
        }
        stage('Test') {
            steps {
                send_msg("[$JOB_NAME] 빌드 시작\nURL: $BUILD_URL")
                script {
                    sh './gradlew clean check --stacktrace'
                }
            }
            post {
                failure {
                    send_msg("[$JOB_NAME] Test fail.")
                }
                success {
                    send_msg("[$JOB_NAME] Test success.")
                }
            }
        }
    }
}
