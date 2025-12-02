# my-blog-back-app

## Требования
1) Для прогона тестов необходимо наличие Docker

## 📦 Установка и запуск
1) git clone https://github.com/swfatumepta/my-blog-back-app.git
2) ./mvn clean build (./mvn clean verify для прогона интеграционных тестов)
3) docker-compose up --build

## 🛠 Особенности
1) Postman коллекци для тестирования API ([my-blog-back-app.postman_collection.json](my-blog-back-app.postman_collection.json))
2) PostgreSQL свои данные будет хрнаить в корне в директории /[db_data](db_data)
3) Изображения, прикрепляемые к постам будут храниться в корне /[images](images)
---
4) Для активации remote debug требуется установить значение переменной ENABLE_DEBUG=true в [.env](.env)
5) Remote DEBUG доступен по localhost:8000
