* **GET** http://localhost:8080/product/all - Запрос к шлюзу на получение всех товаров
* **GET** http://localhost:8080/product/id/1 - Запрос к шлюзу на получение товара по ID

* **POST** http://localhost:8080/product/add - Запрос на добавление нового товара
 ``` json
{
    "title": "Lenovo ThinkPad T14 G2",
    "price": 599.00,
    "description": 70000,
}
 ```

* **PUT** http://localhost:8080/product/update - Запрос на обновление данных товара
 ``` json
{
    "id": 1,
    "title": "Lenovo ThinkPad T14 G2",
    "price": 699.00,
}
 ```
* **DELETE** http://localhost:8080/product/2 - Запрос на удаление товара

***ПРОВЕРКА РАБОТОСПОСОБНОСТИ***
* http://localhost:15672/#/ - Посмотреть работу RabbitMQ (логин: guest, пароль: guest);
* http://localhost:8080/actuator/metrics - Посмотреть, какие метрики отдаёт шлюз;
* http://localhost:8080/actuator/prometheus - Посмотреть сами метрики;
* http://localhost:9090/query - Запросы к Prometheus;
* http://localhost:3000/dashboards - Посмотреть дашборды в Grafana (логин: admin, пароль: admin);
* http://localhost:5601/app/discover - Посмотреть логи в Kibana;