#!/bin/bash

# === Настройки ===
JAR_NAME="travel_time-0.0.1-SNAPSHOT.jar"
JAR_PATH="target/$JAR_NAME"
EC2_USER="ec2-user"
EC2_HOST="13.48.249.158"
PEM_PATH="/d/KeyFolder/JumpBoxKeyPair.pem"

# === Проверка наличия JAR ===
if [ ! -f "$JAR_PATH" ]; then
  echo "Файл $JAR_PATH не найден. Сначала собери проект с помощью './mvnw clean package'"
  exit 1
fi

echo "➡ Загрузка $JAR_NAME на EC2 ($EC2_HOST)..."
scp -i "$PEM_PATH" "$JAR_PATH" "$EC2_USER@$EC2_HOST:/home/ec2-user/"

echo "➡ Подключение и запуск на сервере..."
ssh -i "$PEM_PATH" "$EC2_USER@$EC2_HOST" << EOF
  echo "➡ Остановка предыдущего приложения (если оно работало)..."
  pkill -f "$JAR_NAME" || echo "Предыдущее приложение не найдено."

  echo "➡ Запуск нового приложения в фоне..."
  nohup java -jar "$JAR_NAME" > app.log 2>&1 &

  echo "➡ tail логов:"
  tail -n 10 app.log
EOF
