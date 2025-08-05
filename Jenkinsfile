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
}
