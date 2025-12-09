# Docker Setup для базы данных PostgreSQL

Этот проект включает Docker конфигурацию для запуска PostgreSQL базы данных.

## Быстрый старт

### Вариант 1: Использование docker-compose (рекомендуется)

1. Запустите PostgreSQL контейнер:
```bash
docker-compose up -d
```

2. Проверьте статус контейнера:
```bash
docker-compose ps
```

3. Просмотрите логи:
```bash
docker-compose logs postgres
```

4. Остановите контейнер:
```bash
docker-compose down
```

### Вариант 2: Использование Dockerfile напрямую

1. Соберите образ:
```bash
docker build -f Dockerfile.postgres -t infosec-postgres .
```

2. Запустите контейнер:
```bash
docker run -d \
  --name infosec_postgres \
  -e POSTGRES_DB=infosec_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  infosec-postgres
```

## Конфигурация

### Параметры подключения

После запуска контейнера, используйте следующие параметры в `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/infosec_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Примечание:** Порт изменен на 5433, чтобы избежать конфликта с локальной установкой PostgreSQL (если она есть).

Если приложение также запущено в Docker, используйте имя сервиса:
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/infosec_db
```

### Переменные окружения

Вы можете изменить параметры базы данных через переменные окружения в `docker-compose.yml`:

- `POSTGRES_DB` - имя базы данных (по умолчанию: infosec_db)
- `POSTGRES_USER` - пользователь (по умолчанию: postgres)
- `POSTGRES_PASSWORD` - пароль (по умолчанию: postgres)

## Управление данными

### Просмотр данных

Подключитесь к базе данных через psql:
```bash
docker exec -it infosec_postgres psql -U postgres -d infosec_db
```

### Резервное копирование

Создайте бэкап базы данных:
```bash
docker exec infosec_postgres pg_dump -U postgres infosec_db > backup.sql
```

### Восстановление из бэкапа

```bash
docker exec -i infosec_postgres psql -U postgres infosec_db < backup.sql
```

### Удаление всех данных

```bash
docker-compose down -v
```

⚠️ **Внимание**: Это удалит все данные в базе!

## Health Check

Контейнер включает health check, который проверяет готовность PostgreSQL каждые 10 секунд.

Проверьте статус:
```bash
docker inspect infosec_postgres | grep -A 10 Health
```

## Troubleshooting

### Порт уже занят

Если порт 5433 уже занят, измените маппинг портов в `docker-compose.yml`:
```yaml
ports:
  - "5434:5432"  # Используйте другой внешний порт
```

**Примечание:** По умолчанию используется порт 5433, чтобы избежать конфликта с локальной установкой PostgreSQL.

### Проблемы с правами доступа

Если возникают проблемы с правами доступа к данным, убедитесь что Docker имеет права на создание томов.

### Просмотр логов

```bash
docker-compose logs -f postgres
```

## Производственное использование

⚠️ **Важно**: Для production окружения:

1. Измените пароль по умолчанию
2. Используйте секреты Docker или переменные окружения для паролей
3. Настройте регулярные бэкапы
4. Рассмотрите использование managed PostgreSQL сервиса (AWS RDS, Azure Database, etc.)

