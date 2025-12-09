# Тестирование защиты от XSS (Cross-Site Scripting)

## Как проверить, что пользовательские данные экранируются

### Метод 1: Визуальная проверка в ответах API

#### Шаг 1: Создайте тестового пользователя с опасными данными

В базе данных создан пользователь с потенциально опасным содержимым:
- **Email**: `<script>alert('XSS')</script>`
- **FullName**: `<img src=x onerror=alert(1)>`

#### Шаг 2: Получите данные через API

```powershell
# Получение токена
$loginBody = @{username='testuser';password='testpass123'} | ConvertTo-Json
$response = Invoke-RestMethod -Uri http://localhost:8080/auth/login -Method Post -Body $loginBody -ContentType 'application/json'
$token = $response.token

# Получение пользователя с опасными данными
$headers = @{Authorization="Bearer $token"}
$user = Invoke-RestMethod -Uri http://localhost:8080/users/3 -Method Get -Headers $headers
$user | ConvertTo-Json
```

#### Шаг 3: Проверьте экранирование

**Ожидаемый результат (экранированные данные):**
```json
{
    "id": 3,
    "username": "xss_test",
    "email": "&lt;script&gt;alert('XSS')&lt;/script&gt;",
    "fullName": "&lt;img src=x onerror=alert(1)&gt;"
}
```

**Что означает экранирование:**
- `<` → `&lt;` (HTML entity для символа "меньше")
- `>` → `&gt;` (HTML entity для символа "больше")
- `'` → `&#39;` или `&apos;` (HTML entity для одинарной кавычки)

В JSON ответе это может отображаться как Unicode escape последовательности:
- `\u0026lt;` = `&lt;`
- `\u0026gt;` = `&gt;`
- `\u0027` = `'`

---

### Метод 2: Проверка исходного кода

#### Где происходит экранирование:

1. **UserService.java** - все методы возврата пользовательских данных:
   ```java
   xssSanitizer.sanitize(user.getUsername())
   xssSanitizer.sanitize(user.getEmail())
   xssSanitizer.sanitize(user.getFullName())
   ```

2. **XssSanitizer.java** - использует Apache Commons Text:
   ```java
   StringEscapeUtils.escapeHtml4(input)
   ```

3. **DataController.java** - сообщения также санитизируются:
   ```java
   xssSanitizer.sanitize("List of users retrieved successfully")
   ```

---

### Метод 3: Автоматизированный тест

Создайте тестовый скрипт для проверки экранирования:

```powershell
# Тест экранирования XSS
function Test-XSSProtection {
    # Получение токена
    $loginBody = @{username='testuser';password='testpass123'} | ConvertTo-Json
    $response = Invoke-RestMethod -Uri http://localhost:8080/auth/login -Method Post -Body $loginBody -ContentType 'application/json'
    $token = $response.token
    $headers = @{Authorization="Bearer $token"}
    
    # Получение пользователя с опасными данными
    $user = Invoke-RestMethod -Uri http://localhost:8080/users/3 -Method Get -Headers $headers
    
    # Проверка экранирования
    $emailEscaped = $user.email -match '&lt;|&gt;|&#39;|\u0026lt;|\u0026gt;'
    $fullNameEscaped = $user.fullName -match '&lt;|&gt;|&#39;|\u0026lt;|\u0026gt;'
    
    if ($emailEscaped -and $fullNameEscaped) {
        Write-Host "✅ XSS защита работает: данные экранированы" -ForegroundColor Green
        Write-Host "Email: $($user.email)"
        Write-Host "FullName: $($user.fullName)"
    } else {
        Write-Host "❌ XSS защита не работает: данные НЕ экранированы!" -ForegroundColor Red
    }
}

Test-XSSProtection
```

---

### Метод 4: Проверка через браузер (если есть фронтенд)

Если данные отображаются в HTML:

1. **Без экранирования (опасно):**
   ```html
   <div>Email: <script>alert('XSS')</script></div>
   ```
   → JavaScript код выполнится!

2. **С экранированием (безопасно):**
   ```html
   <div>Email: &lt;script&gt;alert('XSS')&lt;/script&gt;</div>
   ```
   → Отображается как текст, код НЕ выполняется

---

## Что экранируется

### Символы, которые экранируются:

| Символ | HTML Entity | Описание |
|--------|-------------|----------|
| `<` | `&lt;` | Меньше (начало тега) |
| `>` | `&gt;` | Больше (конец тега) |
| `&` | `&amp;` | Амперсанд |
| `"` | `&quot;` | Двойная кавычка |
| `'` | `&#39;` или `&apos;` | Одинарная кавычка |

### Примеры опасных конструкций, которые блокируются:

1. **JavaScript в тегах:**
   - `<script>alert('XSS')</script>` → `&lt;script&gt;alert('XSS')&lt;/script&gt;`

2. **JavaScript в атрибутах:**
   - `<img src=x onerror=alert(1)>` → `&lt;img src=x onerror=alert(1)&gt;`

3. **JavaScript в событиях:**
   - `<div onclick="alert('XSS')">` → `&lt;div onclick="alert('XSS')"&gt;`

4. **HTML теги:**
   - `<b>Bold</b>` → `&lt;b&gt;Bold&lt;/b&gt;`

---

## Проверка всех эндпоинтов

### GET /api/data

```powershell
$loginBody = @{username='testuser';password='testpass123'} | ConvertTo-Json
$response = Invoke-RestMethod -Uri http://localhost:8080/auth/login -Method Post -Body $loginBody -ContentType 'application/json'
$token = $response.token
$headers = @{Authorization="Bearer $token"}
$allUsers = Invoke-RestMethod -Uri http://localhost:8080/api/data -Method Get -Headers $headers
$allUsers.users | Where-Object { $_.username -eq 'xss_test' } | ConvertTo-Json
```

**Проверьте:** Все поля пользователя должны быть экранированы.

### GET /users/{id}

```powershell
$user = Invoke-RestMethod -Uri http://localhost:8080/users/3 -Method Get -Headers $headers
$user | ConvertTo-Json
```

**Проверьте:** Все поля пользователя должны быть экранированы.

---

## Вывод

✅ **Все пользовательские данные экранируются перед возвратом в ответах API**

Это подтверждается:
1. ✅ Кодом: `XssSanitizer` используется во всех методах возврата данных
2. ✅ Тестами: Данные с опасным содержимым экранируются в ответах
3. ✅ Библиотекой: Используется проверенная библиотека Apache Commons Text

**Защита от XSS работает корректно!**

