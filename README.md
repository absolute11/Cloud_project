# Облачное Хранилище

## Описание

Облачное Хранилище — это REST-сервис, разработанный на базе Spring Boot, который предлагает аутентифицированным пользователям возможность загружать и управлять файлами. Этот сервис интегрируется с существующим веб-приложением, предоставляя функциональность для аутентификации пользователя, загрузки файлов и их списка.

## Особенности

- Интеграция RESTful API.
- Аутентификация пользователей и управление токенами.
- Загрузка, получение и удаление файлов.
- Использование PostgreSQL для постоянного хранения данных.
- Развертывание через Docker и Docker Compose.

## Технологический стек

- **Spring Boot**: Фреймворк для создания автономных, готовых к производству Spring-приложений.
- **PostgreSQL**: Реляционная база данных.
- **Docker**: Контейнеризация приложения и базы данных.
- **Maven**: Управление зависимостями и автоматизация сборки.
- **JWT**: Для безопасной и безсостоянийной аутентификации.
- **Spring Security**: Для аутентификации и авторизации.

##API Endpoints
#Доступны следующие конечные точки:

- **POST /cloud/login**: Аутентификация пользователей и возврат JWT.
- **POST /cloud/logout**: Инвалидация JWT пользователя.
- **GET /cloud/list**: Список всех файлов, загруженных аутентифицированным пользователем.
- **POST /cloud/file**: Загрузка нового файла.
- **DELETE /cloud/file**: Удаление существующего файла.
- **GET /cloud/file**: Скачивание определенного файла.
- **PUT /cloud/file**: Переименование существующего файла.