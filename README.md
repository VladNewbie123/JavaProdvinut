📌 Команди для отримання курсу валют та збереження в Excel
1️⃣ Отримати курс валюти (наприклад, USD)
curl http://localhost:8080/api/exchange-rate/USD

2️⃣ Отримати курс для іншої валюти (наприклад, EUR)
curl http://localhost:8080/api/exchange-rate/EUR
✅ Якщо курс є в базі, він відобразиться у JSON

3️⃣ Зберегти курси в Excel (rates.xlsx)
Редактировать
curl -o rates.xlsx http://localhost:8080/api/exchange-rate/export
✅ Excel-файл rates.xlsx збережеться в папці, де виконується команда.
✅ Відкрий rates.xlsx у Excel або LibreOffice
