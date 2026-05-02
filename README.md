# Stock Trading System

데이터 정합성, 동시성, 대용량 처리 관점을 중심으로 증권 거래 시스템을 설계하고 구현해보는 토이 프로젝트입니다.

이 저장소의 목표는 단순 CRUD 구현보다, 주문과 잔고처럼 돈이 오가는 시스템을 어떻게 더 안전하고 확장 가능하게 설계할지 경험하는 데 있습니다.

## Why This Project

- 주문, 체결, 잔고, 시세를 서로 다른 책임으로 분리하는 구조를 연습합니다.
- 강한 정합성이 필요한 쓰기 모델과 빠른 응답이 중요한 읽기 모델을 다르게 설계합니다.
- 동시 주문, 중복 요청, 장애 복구, 이벤트 재처리 같은 실전 이슈를 다룹니다.
- 모놀리식으로 시작하되 이후 분리 가능한 경계를 코드와 문서에 남깁니다.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Redis
- Kafka
- Flyway
- Gradle

## Architecture Focus

이 프로젝트는 아래 관점을 중심으로 확장할 예정입니다.

- Transaction boundary
  주문 접수와 잔고 예약은 하나의 정합성 경계로 묶습니다.
- Outbox pattern
  DB 반영과 이벤트 발행 간 불일치를 줄이기 위해 outbox를 사용합니다.
- CQRS-friendly structure
  읽기와 쓰기를 같은 방식으로 다루지 않도록 구조를 분리합니다.
- Idempotency
  중복 주문 요청이 들어와도 같은 주문이 두 번 처리되지 않도록 설계합니다.
- Concurrency control
  잔고 차감과 같은 경쟁 구간에서 정합성이 깨지지 않도록 제어 전략을 실험합니다.

자세한 내용은 [docs/architecture.md](docs/architecture.md)에서 볼 수 있습니다.

## Current Scope

현재 저장소에는 아래 초기 범위가 포함되어 있습니다.

1. 주문 접수 API
2. 계좌 현금 잔고 및 예약 금액 모델
3. 주문/잔고/outbox 초기 스키마
4. 주문 수락 이벤트 저장 구조
5. 로컬 실행용 PostgreSQL, Redis, Kafka 구성

## Project Structure

```text
.
├── docs
│   └── architecture.md
├── src
│   ├── main
│   │   ├── java/com/toybroker/trading
│   │   └── resources
│   │       ├── application.yml
│   │       └── db/migration
├── docker-compose.yml
├── build.gradle
└── settings.gradle
```

## Getting Started

### 1. Start infrastructure

```bash
docker compose up -d
```

### 2. Run the application

```bash
./gradlew bootRun
```

### 3. Health check

```bash
curl http://localhost:8080/api/system/health
```

예상 응답:

```json
{"success":true,"data":{"status":"UP","project":"stock-trading-system"},"message":null}
```

## Example API

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "11111111-1111-1111-1111-111111111111",
    "symbol": "005930",
    "side": "BUY",
    "orderType": "LIMIT",
    "quantity": 10,
    "price": 70000,
    "idempotencyKey": "order-001"
  }'
```

## Next Steps

- 체결 엔진 흐름 추가
- 주문/체결/잔고 조회 모델 보강
- outbox publisher 구현
- 동시성 테스트 추가
- Redis 기반 조회 최적화 실험
- Kafka consumer 및 재처리 전략 보강

## Notes

- 현재는 Gradle 기반 프로젝트입니다.
- 이 환경에는 `gradle`이 설치되어 있지 않아 wrapper 파일(`gradlew`, `gradle/wrapper/*`)은 아직 생성하지 못했습니다.
- 로컬에 Gradle이 설치된 환경에서는 `gradle wrapper` 실행 후 `./gradlew bootRun`으로 고정해서 쓰는 것을 추천합니다.
- Docker DB 이름은 `stock_trading_system`입니다.
- 의존성 다운로드와 원격 푸시는 환경에 따라 별도 권한 또는 인증이 필요할 수 있습니다.
