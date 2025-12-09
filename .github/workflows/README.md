# GitHub Actions CI/CD Pipeline

## Описание

Этот pipeline автоматически запускает проверки безопасности при каждом push или создании pull request.

## Что проверяется

### SAST (Static Application Security Testing)

1. **SpotBugs** - статический анализ кода на поиск багов и потенциальных проблем
2. **PMD** - анализ качества кода и поиск проблемных паттернов
3. **Checkstyle** - проверка соответствия кода стандартам стиля

### SCA (Software Composition Analysis)

1. **OWASP Dependency-Check** - проверка зависимостей на известные уязвимости (CVE)
   - Используется Maven плагин
   - Также запускается standalone версия для более полного анализа
   - Проверяет все зависимости проекта на наличие известных уязвимостей

## Триггеры

Pipeline автоматически запускается при:
- Push в ветки: `main`, `master`, `develop`
- Создании Pull Request в эти ветки

## Отчеты

Все отчеты сохраняются как артефакты GitHub Actions и доступны в течение 30 дней:
- `dependency-check-maven-reports` - отчеты OWASP Dependency-Check (Maven)
- `dependency-check-standalone-reports` - отчеты OWASP Dependency-Check (Standalone)
- `spotbugs-reports` - отчеты SpotBugs
- `pmd-reports` - отчеты PMD
- `checkstyle-reports` - отчеты Checkstyle
- `test-reports` - отчеты тестов

## Настройка

### Порог критичности уязвимостей

По умолчанию pipeline не падает при обнаружении уязвимостей (для информационных целей).
Чтобы изменить это поведение, измените параметр `--failOnCVSS` в workflow файле.

### Подавление ложных срабатываний

Для OWASP Dependency-Check используйте файл `dependency-check-suppressions.xml` в корне проекта.

## Локальный запуск

### OWASP Dependency-Check
```bash
mvn org.owasp:dependency-check-maven:check
```

### SpotBugs
```bash
mvn spotbugs:check
```

### PMD
```bash
mvn pmd:check
```

### Checkstyle
```bash
mvn checkstyle:check
```

## Дополнительная информация

- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [SpotBugs](https://spotbugs.github.io/)
- [PMD](https://pmd.github.io/)
- [Checkstyle](https://checkstyle.sourceforge.io/)

