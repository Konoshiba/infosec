# Настройка CI/CD Pipeline для безопасности

## Обзор

Настроен автоматический CI/CD pipeline для проверки безопасности кода с использованием SAST и SCA инструментов.

## Структура файлов

```
.github/
└── workflows/
    ├── ci.yml              # Основной workflow для проверок безопасности
    └── README.md           # Документация по pipeline

dependency-check-suppressions.xml  # Подавление ложных срабатываний OWASP
checkstyle.xml                     # Конфигурация Checkstyle
```

## Инструменты безопасности

### SAST (Static Application Security Testing)

1. **SpotBugs** (v4.8.2.1)
   - Статический анализ кода на поиск багов
   - Находит потенциальные проблемы в коде
   - Отчет: `target/spotbugsXml.xml`

2. **PMD** (v3.21.2)
   - Анализ качества кода
   - Проверка на проблемные паттерны
   - Правила: security, bestpractices, errorprone
   - Отчет: `target/pmd.xml`

3. **Checkstyle** (v3.3.1)
   - Проверка стиля кода
   - Соответствие стандартам кодирования
   - Отчет: `target/checkstyle-result.xml`

### SCA (Software Composition Analysis)

1. **OWASP Dependency-Check** (v9.0.9)
   - Проверка зависимостей на известные уязвимости (CVE)
   - Используется в двух режимах:
     - Maven плагин (быстрый анализ)
     - Standalone версия (полный анализ)
   - Порог критичности: CVSS 7.0
   - Отчеты: HTML, JSON, XML, CSV

## Автоматический запуск

Pipeline автоматически запускается при:
- ✅ Push в ветки: `main`, `master`, `develop`
- ✅ Создании Pull Request в эти ветки

## Локальный запуск

### Запуск всех проверок

```bash
# OWASP Dependency-Check
mvn org.owasp:dependency-check-maven:check

# SpotBugs
mvn spotbugs:check

# PMD
mvn pmd:check

# Checkstyle
mvn checkstyle:check

# Все проверки сразу
mvn clean verify
```

### Просмотр отчетов

После запуска проверок отчеты будут в директории `target/`:
- `target/dependency-check-report.html` - OWASP Dependency-Check
- `target/spotbugsXml.xml` - SpotBugs
- `target/pmd.xml` - PMD
- `target/checkstyle-result.xml` - Checkstyle

## Настройка

### Изменение порога критичности уязвимостей

В файле `pom.xml` измените параметр `failBuildOnCVSS`:

```xml
<configuration>
    <failBuildOnCVSS>7</failBuildOnCVSS>  <!-- Измените значение -->
</configuration>
```

### Подавление ложных срабатываний

Отредактируйте файл `dependency-check-suppressions.xml`:

```xml
<suppress>
    <notes><![CDATA[
    Описание причины подавления
    ]]></notes>
    <cve>CVE-XXXX-XXXX</cve>
</suppress>
```

### Настройка Checkstyle

Отредактируйте файл `checkstyle.xml` для изменения правил проверки.

## Отчеты в GitHub Actions

После каждого запуска pipeline все отчеты сохраняются как артефакты:
- Доступны в разделе "Actions" → "Artifacts"
- Хранятся 30 дней
- Можно скачать для локального просмотра

## Интеграция с IDE

### IntelliJ IDEA

1. Установите плагины:
   - SpotBugs
   - PMD
   - Checkstyle-IDEA

2. Настройте Checkstyle:
   - File → Settings → Tools → Checkstyle
   - Укажите путь к `checkstyle.xml`

### Eclipse

1. Установите плагины через Marketplace:
   - SpotBugs
   - PMD
   - Checkstyle

## Best Practices

1. **Регулярно обновляйте зависимости**
   - OWASP Dependency-Check поможет найти уязвимости
   - Обновляйте уязвимые зависимости

2. **Исправляйте найденные проблемы**
   - SAST инструменты находят реальные проблемы
   - Не игнорируйте предупреждения

3. **Настраивайте правила под проект**
   - Адаптируйте Checkstyle под ваш стиль кода
   - Настраивайте PMD правила

4. **Проверяйте отчеты регулярно**
   - Скачивайте отчеты из GitHub Actions
   - Анализируйте результаты

## Troubleshooting

### Pipeline падает с ошибкой

1. Проверьте логи в GitHub Actions
2. Запустите проверки локально
3. Убедитесь, что все зависимости доступны

### Ложные срабатывания

1. Добавьте в `dependency-check-suppressions.xml`
2. Настройте правила в `checkstyle.xml` или `pom.xml`

### Медленный запуск

1. Используйте кэширование Maven зависимостей
2. Рассмотрите запуск проверок параллельно
3. Используйте только необходимые инструменты

## Дополнительные ресурсы

- [OWASP Dependency-Check Documentation](https://jeremylong.github.io/DependencyCheck/)
- [SpotBugs Documentation](https://spotbugs.github.io/)
- [PMD Documentation](https://pmd.github.io/)
- [Checkstyle Documentation](https://checkstyle.sourceforge.io/)

## Поддержка

При возникновении проблем:
1. Проверьте логи GitHub Actions
2. Запустите проверки локально для отладки
3. Проверьте версии инструментов в `pom.xml`

