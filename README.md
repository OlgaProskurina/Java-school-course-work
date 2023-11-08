## Описание

Фул стек приложение - справочник документов

## Структура

- `backend` - Бэкенд на java.
- `ui` - Фронтенд на react + redux.

## Подготовка

Установите:

- [node](https://nodejs.org) - front
- [openjdk](https://openjdk.java.net) 15 - java бэк
- [docker](https://www.docker.com/get-started/)

## Запуск в докере

### Сборка фронта

```
./gradlew ui:build
```
### Сборка бека
```
./gradlew backend:bootJar
```

### Запуск через docker-compose
```
docker-compose up
```

### Адрес страницы
```
http://localhost:9000/#/
```
## Использование
Создать документ и отправить в обработку. После этого в топик response-document нужно отправить сообщение вида:
```json
 {
 "documentId": 1,
 "status": "ACCEPTED"
 }
```
Где documentId - это номер документа, отправленного в обработку, а 
status - результат обработки документа, может быть или ACCEPTED или DECLINED.