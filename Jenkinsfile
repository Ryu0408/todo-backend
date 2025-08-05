import groovy.json.JsonOutput

pipeline {
  agent any

  triggers {
    githubPush()
  }

  environment {
    SSH_TARGET = "ubuntu@43.200.12.227"
    SSH_KEY_ID = "jenkins-todo-backend-key"
    PROJECT_DIR = "/home/ubuntu/apps"
    BACKEND_DIR = "$PROJECT_DIR/todo-backend"
    SLACK_WEBHOOK = credentials('SLACK_WEBHOOK') // 동일한 Webhook 사용
  }

  stages {
    stage('Test todo-backend') {
      steps {
        sshagent(credentials: [SSH_KEY_ID]) {
          sh """
            ssh -o StrictHostKeyChecking=no $SSH_TARGET '
              cd $BACKEND_DIR &&
              echo "[🔍] 백엔드 테스트 시작" &&
              ./gradlew test || exit 1
            '
          """
        }
      }
    }

    stage('Deploy todo-backend only') {
      steps {
        sshagent(credentials: [SSH_KEY_ID]) {
          sh """
            ssh -o StrictHostKeyChecking=no $SSH_TARGET '
              echo "[1] ✅ todo-backend 이동 및 git pull"
              cd $BACKEND_DIR &&
              git pull origin main

              echo "[2] 📦 docker-compose 실행 디렉토리 이동"
              cd $PROJECT_DIR

              echo "[3] 🧹 docker-compose down"
              docker-compose down

              echo "[4] 🛠️ docker-compose build"
              docker-compose build

              echo "[5] 🚀 docker-compose up -d"
              docker-compose up -d
            '
          """
        }
      }
    }
  }

  post {
    success {
      script {
        def payload = [
          username: "Jenkins Todo Notifier",
          text: "✅ todo-backend 배포 성공!!\n코드가 성공적으로 배포되었습니다 🚀"
        ]

        httpRequest(
          httpMode: 'POST',
          contentType: 'APPLICATION_JSON',
          requestBody: JsonOutput.toJson(payload),
          url: SLACK_WEBHOOK
        )
      }
    }

    failure {
      script {
        def payload = [
          username: "Jenkins Todo Notifier",
          text: "❌ todo-backend 배포 실패!\n배포 중 문제가 발생했습니다. 콘솔 로그를 확인하세요."
        ]

        httpRequest(
          httpMode: 'POST',
          contentType: 'APPLICATION_JSON',
          requestBody: JsonOutput.toJson(payload),
          url: SLACK_WEBHOOK
        )
      }
    }
  }
}
