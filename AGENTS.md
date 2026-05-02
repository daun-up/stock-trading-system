<!-- @format -->

# AGENTS.md

## 프로젝트 목적

이 저장소는 데이터 정합성, 동시성, 멱등성, 이벤트 기반 확장성을 중심으로 증권 거래 시스템을 연습하는 Java 21 / Spring Boot 토이 프로젝트다.

이 프로젝트는 일반적인 CRUD 예제가 아니라 트레이딩 도메인 학습용이다. 변경 작업을 할 때는 `README.md`와 `docs/architecture.md`에 적힌 아래 핵심 방향을 유지한다.

- 주문은 유실되면 안 된다.
- 잔고 변경은 항상 정확해야 한다.
- 중복 요청과 중복 이벤트는 재시도되어도 안전해야 한다.
- 지금은 모놀리식이지만, 이후 분리 가능한 내부 경계를 유지해야 한다.

## 기본 언어 규칙

- 사용자가 한국어로 요청하면 사용자-facing 설명은 한국어로 작성한다.
- 코드, 식별자, 클래스명, 메서드명은 기존 코드 스타일을 따라 영어를 유지한다.
- 커밋 메시지는 한국어 또는 영어 모두 가능하지만, 형식은 아래 커밋 컨벤션을 따른다.
- 주석은 꼭 필요한 경우에만 짧게 쓴다.

## 기술 스택

- Java 21
- Spring Boot 3.5.x
- Gradle Wrapper `./gradlew`
- Spring Web
- Spring Validation
- Spring Data JPA
- Spring Actuator
- PostgreSQL
- Flyway
- Kafka
- Redis
- Docker Compose

## 저장소 구조

- `src/main/java/com/toybroker/trading/account`: 계좌 및 잔고 도메인
- `src/main/java/com/toybroker/trading/order`: 주문 API, 애플리케이션 서비스, 주문 도메인
- `src/main/java/com/toybroker/trading/outbox`: 트랜잭셔널 아웃박스
- `src/main/java/com/toybroker/trading/common`: 공통 응답 및 공용 코드
- `src/main/java/com/toybroker/trading/system`: 시스템/헬스체크 엔드포인트
- `src/main/resources/db/migration`: Flyway 마이그레이션
- `docs`: 아키텍처, 셋업, 배포 관련 문서

새 패키지는 이 경계를 존중해서 추가한다. 기능상 필요할 때만 `execution`, `marketdata`, read model 관련 패키지를 새로 만든다.

## 실행 및 빌드 명령

전역 Gradle 대신 반드시 Wrapper를 사용한다.

```bash
./gradlew test
./gradlew bootRun
./gradlew clean build
```

로컬 인프라 실행:

```bash
docker compose up -d
docker compose down
```

애플리케이션 실행 후 헬스 체크:

```bash
curl http://localhost:8080/api/system/health
```

예상 응답 형식:

```json
{
  "success": true,
  "data": { "status": "UP", "project": "stock-trading-system" },
  "message": null
}
```

## 검증 규칙

코드 변경을 마치기 전에 가능한 가장 좁고 적절한 검증을 수행한다.

- 일반적인 Java 코드 변경: `./gradlew test`
- 스키마 변경 또는 애플리케이션 wiring 변경: `./gradlew clean build`
- API 동작 변경: `docker compose up -d` 후 `./gradlew bootRun`으로 앱을 띄우고 `curl`로 관련 엔드포인트 확인

Docker, 네트워크, 자격 증명, 로컬 서비스 부재 등으로 명령을 실행하지 못한 경우에는 어떤 명령을 시도했고 왜 막혔는지 분명하게 남긴다.

## 도메인 규칙

- 비즈니스 불변식은 컨트롤러가 아니라 도메인 또는 애플리케이션 서비스에 둔다.
- 주문 생성, 잔고 예약, outbox 저장은 함께 성공하거나 함께 실패해야 하면 하나의 트랜잭션으로 묶는다.
- 주문 멱등성은 `idempotencyKey`를 통해 유지한다.
- 금액과 수량은 `BigDecimal`을 사용한다. 부동소수점 타입은 사용하지 않는다.
- 외부 요청 검증은 API 경계에서 Jakarta Validation을 우선 사용한다.
- Aggregate 식별자는 UUID를 일관되게 사용한다.
- 주문 상태나 유형은 문자열 하드코딩 대신 enum으로 모델링한다.

## 트랜잭션 및 정합성 규칙

- 쓰기 모델의 source of truth는 PostgreSQL이다.
- 요청 트랜잭션 안에서 상태를 저장하면서 Kafka를 직접 발행하는 구조는 피한다. outbox 경로가 같이 보장될 때만 허용한다.
- outbox 레코드는 상태 변경과 같은 트랜잭션 안에서 저장한다.
- 컨슈머나 재처리 가능한 프로세서를 추가할 때는 중복 처리 방식을 코드에 명시한다.
- 잔고 예약과 해제 로직은 특히 조심해서 다룬다. `available_cash`가 음수가 되는 변화는 의도된 설계 결정일 때만 허용한다.

## 데이터베이스 및 Flyway 규칙

- 모든 스키마 변경은 `src/main/resources/db/migration` 아래 Flyway 마이그레이션으로 관리한다.
- 이미 적용된 마이그레이션은 로컬 초기화 상황이 아닌 이상 수정하지 않는다.
- 새 마이그레이션 파일명은 `V<number>__description.sql` 형식을 따른다.
- 트레이딩 데이터에 맞는 타입을 유지한다.
- 금액과 수량은 기본적으로 `NUMERIC(19, 4)`를 사용한다.
- 내부 식별자는 `UUID`를 사용한다.
- 시간 값은 가능하면 `TIMESTAMP WITH TIME ZONE`을 사용한다.
- 새로운 조회 패턴이 생기면 필요한 인덱스를 함께 추가한다.

## API 규칙

- 공개 API 응답은 `ApiResponse` 형식을 사용한다.
- 컨트롤러는 얇게 유지한다. 요청 검증과 서비스 위임에 집중한다.
- 명령 처리 로직은 애플리케이션 서비스에 둔다.
- 엔드포인트 경로는 `/api` 아래에 둔다.
- 외부에서 바로 써볼 수 있는 엔드포인트를 추가했다면 `README.md` 또는 관련 문서에 간단한 `curl` 예시를 남긴다.

## 테스트 가이드

현재 `src/test` 디렉터리는 아직 없다. 의미 있는 동작을 추가할 때는 변경한 경계에 맞는 테스트를 같이 추가한다.

- 도메인 불변식: 빠른 단위 테스트
- 애플리케이션 서비스의 트랜잭션/멱등성 동작: 영속성이 필요한 Spring 테스트
- API 검증/응답 형식: 웹 레이어 또는 통합 테스트

우선순위가 높은 테스트 대상:

- 중복 주문 요청 처리
- 잔고 부족 처리
- 예약 금액 계산
- outbox 생성 여부
- 주문 상태 전이

## 문서화 규칙

동작이나 아키텍처 방향이 바뀌면 문서도 함께 갱신한다.

- `README.md`: 현재 범위, 실행 방법, 예시 요청
- `docs/architecture.md`: 도메인 경계, 정합성 전략, 확장 방향
- `docs/setup-history.md`: 환경 구성 및 도구 선택 이력
- `docs/aws-free-tier-deployment.md`: 배포, 보안, 시크릿 관리 관련 결정

문서는 길게 쓰기보다 실제 결정과 트레이드오프를 짧고 실용적으로 남긴다.

## 보안 및 시크릿 규칙

- 실제 비밀번호, API 키, AWS 키, 토큰은 커밋하지 않는다.
- 로컬 값은 `.env` 또는 환경 변수로 관리한다.
- `.env.example`에는 안전한 예시 값만 둔다.
- 운영 환경 설정은 `README.md`에 적힌 것처럼 환경 변수 또는 별도 시크릿 저장소를 통해 주입한다.

## Git 작업 규칙

- 수정 전과 작업 마무리 전에는 `git status --short`로 변경 범위를 확인한다.
- 사용자가 만든 관련 없는 변경은 되돌리지 않는다.
- 요청 범위를 벗어나는 리팩터링은 하지 않는다.
- 파괴적인 git 명령은 사용자가 명시적으로 요청한 경우에만 실행한다.

## 커밋 컨벤션

커밋 메시지는 아래 형식을 기본으로 사용한다.

```text
type(scope): subject
```

규칙:

- `type`은 소문자로 쓴다.
- `scope`는 선택 사항이지만 가능하면 붙인다.
- `subject`는 한 줄로 짧고 구체적으로 쓴다.
- 마침표는 붙이지 않는다.
- 한 커밋에는 하나의 의도를 담는다.

권장 `type`:

- `feat`: 기능 추가
- `fix`: 버그 수정
- `refactor`: 동작 변화 없는 구조 개선
- `test`: 테스트 추가 또는 수정
- `docs`: 문서 수정
- `chore`: 빌드, 설정, 기타 자잘한 작업
- `perf`: 성능 개선

예시:

```text
feat(order): API 추가
fix(account): Account 갱신 안 되던 문제 수정
docs(readme): 아키텍처 관련 문서 추가
```

여러 성격의 변경이 섞이면 기능 또는 사용자 영향이 큰 쪽을 기준으로 `type`을 정한다.

## 코드 스타일 선호

- 현재처럼 constructor injection 스타일을 유지한다.
- 단순한 immutable command/result DTO는 주변 코드와 맞는 경우 record를 우선 고려한다.
- 서비스 메서드는 트랜잭션 경계와 도메인 판단이 눈에 보일 정도로 작게 유지한다.
- 중복이나 명확한 경계가 생기기 전까지 추상화를 서두르지 않는다.
- 서비스에서 범용 쿼리 로직을 늘리기보다 의도가 드러나는 repository 메서드를 선호한다.
