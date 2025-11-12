
README
------
Кратко: стартовый skeleton для backend на Java + Spring Boot (MVP).
- H2 database (встроенная) для удобного старта
- Spring Data JPA
- Spring Web
- Spring Security (basic) для MVP
- Модель данных: User (с ролями), Subject, ClassEntity, Student, Grade, Attendance
- Репозитории, базовые контроллеры, сервисы


Как запустить:
1) Установить JDK 17+
2) mvn spring-boot:run
3) Открыть H2 Console: http://localhost:8080/h2-console (jdbc:h2:mem:schooldb)
4) API доступно на http://localhost:8080/api/**


Примечание: это минимальный рабочий каркас. Дальше — добавить: JWT, подробную валидацию, роли и разрешения, unit/integration тесты, frontend.


Дальше — roadmap разработки (что делать следующими итерациями):
1) Добавить CRUD контроллеры для Subject, ClassEntity, Student, Grade, Attendance
2) Добавить DTO и маппинг, валидацию (Spring Validation)
3) Внедрить RBAC: более тонкая модель разрешений (учитель может выставлять оценки только по своим предметам)
4) Заменить Basic Auth на JWT + refresh tokens
5) Добавить unit и integration тесты
6) Подключить PostgreSQL и миграции Flyway/Liquibase
7) Подключить frontend (React) и реализовать UI для ролей
8) Производственный деплой: Docker, CI/CD, мониторинг