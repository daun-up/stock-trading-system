# API Testing

이 프로젝트는 Postman과 Swagger UI 둘 다로 API를 테스트할 수 있습니다.

## 서버 실행

```bash
docker compose up -d

export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
./gradlew bootRun
```

## Swagger UI

브라우저에서 아래 주소를 엽니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON은 아래 주소에서 확인할 수 있습니다.

```text
http://localhost:8080/v3/api-docs
```

Swagger UI에서 `POST /api/orders`를 펼친 뒤 `Try it out`을 누르고 아래 JSON을 넣어 테스트합니다.

```json
{
  "accountId": "11111111-1111-1111-1111-111111111111",
  "symbol": "005930",
  "side": "BUY",
  "orderType": "LIMIT",
  "quantity": 10,
  "price": 70000,
  "idempotencyKey": "order-swagger-001"
}
```

`idempotencyKey`는 중복 요청 방지 키라서 새 주문을 만들고 싶으면 값을 바꿉니다.

## Postman

Postman에서 새 요청을 만들고 아래처럼 설정합니다.

```text
Method: POST
URL: http://localhost:8080/api/orders
Header: Content-Type: application/json
```

Body는 `raw`와 `JSON`을 선택하고 아래 값을 넣습니다.

```json
{
  "accountId": "11111111-1111-1111-1111-111111111111",
  "symbol": "005930",
  "side": "BUY",
  "orderType": "LIMIT",
  "quantity": 10,
  "price": 70000,
  "idempotencyKey": "order-postman-001"
}
```

헬스체크는 아래 GET 요청으로 확인합니다.

```text
GET http://localhost:8080/api/system/health
```
