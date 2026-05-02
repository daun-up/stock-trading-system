# Project Checklist

이 문서는 지금까지 완료한 일과 앞으로 진행할 일을 체크리스트로 관리하기 위한 기준입니다.

## 완료한 일

- [x] Java 21 / Spring Boot / Gradle 프로젝트 초기화
- [x] PostgreSQL, Redis, Kafka 로컬 Docker Compose 구성
- [x] 주문 접수 API 추가
- [x] 계좌 현금 잔고 및 예약 금액 모델 추가
- [x] 주문, 잔고, outbox 초기 스키마 추가
- [x] Flyway 마이그레이션 구성
- [x] 주문 접수 시 잔고 예약, 주문 저장, outbox 이벤트 저장을 하나의 트랜잭션으로 묶기
- [x] idempotencyKey 기반 중복 주문 방지
- [x] 시스템 health endpoint 추가
- [x] 루트 경로 `/`에서 기본 상태 응답 제공
- [x] Kafka 이미지 태그 문제 수정
- [x] Flyway PostgreSQL 모듈 추가
- [x] Swagger/OpenAPI 테스트 지원 추가
- [x] Postman, Swagger UI 테스트 문서 추가
- [x] AWS 프리티어 배포 방향 문서화
- [x] Dockerfile, `.dockerignore`, `.env.example` 추가
- [x] 운영 설정을 환경변수 기반 `application-prod.yml`로 분리
- [x] GitHub 업로드 전 secret 제외 규칙 정리
- [x] Kafka와 Redis 선택 이유 문서화
- [x] AGENTS.md에 프로젝트 작업 규칙 정리
- [x] 현재 스키마 기준 ERD 문서화

## 다음 우선순위

### 1. 주문 도메인 안정화

- [ ] 잔고 부족 시 명확한 예외와 API 응답 처리 추가
- [ ] 매수 주문 금액 계산 규칙 보강
- [ ] 매도 주문을 위한 보유 주식/포지션 모델 설계
- [ ] 주문 취소 흐름 추가
- [ ] 주문 상태 전이 규칙을 테스트로 고정

### 2. 테스트 추가

- [ ] `src/test` 디렉터리 구성
- [ ] `AccountBalance` 잔고 예약 단위 테스트
- [ ] `OrderService` 중복 주문 요청 테스트
- [ ] 잔고 부족 주문 실패 테스트
- [ ] 주문 접수 시 outbox 이벤트 생성 테스트
- [ ] 주문 API validation 테스트

### 3. 예외 처리와 API 응답 정리

- [ ] 공통 예외 응답 구조 추가
- [ ] validation 실패 응답 형식 통일
- [ ] 도메인 예외 타입 추가
- [ ] 400/404/409 등 HTTP status 기준 정리

### 4. Outbox publisher

- [ ] `published = false` outbox 이벤트 조회 로직 추가
- [ ] Kafka topic으로 이벤트 발행
- [ ] 발행 성공 시 `published = true` 처리
- [ ] 발행 실패 시 재시도 전략 설계
- [ ] 중복 발행 가능성을 고려한 consumer idempotency 기준 문서화

### 5. 조회 API와 CQRS 확장

- [ ] 주문 단건 조회 API 추가
- [ ] 계좌별 주문 목록 조회 API 추가
- [ ] 계좌 잔고 조회 API 추가
- [ ] 읽기 모델 분리 기준 문서화
- [ ] Redis 캐시 적용 후보 정리

### 6. 체결 엔진 흐름

- [ ] 체결 도메인 모델 설계
- [ ] 주문 체결 이벤트 설계
- [ ] 체결 시 잔고 확정/예약 해제 흐름 추가
- [ ] 부분 체결 상태 모델링
- [ ] 체결 이벤트 outbox 저장

### 7. 동시성 실험

- [ ] 동시 매수 요청에서 잔고 정합성 검증 테스트
- [ ] pessimistic lock / optimistic lock 중 선택 기준 실험
- [ ] idempotencyKey 동시 요청 race condition 테스트
- [ ] DB isolation level 영향 정리

### 8. AWS 프리티어 배포

- [ ] AWS Budget 알림 생성
- [ ] RDS PostgreSQL 프리티어 인스턴스 생성
- [ ] EC2 프리티어 인스턴스 생성
- [ ] EC2에 Docker 설치
- [ ] 환경변수 또는 `.env`로 운영 설정 주입
- [ ] Docker 이미지 빌드 및 실행
- [ ] `/api/system/health` 외부 접속 확인
- [ ] 보안 그룹에서 필요한 포트만 열기
- [ ] 운영 secret을 GitHub에 올리지 않는지 재확인

### 9. CI/CD

- [ ] GitHub Actions로 `./gradlew test` 자동 실행
- [ ] secret scanning 또는 push protection 확인
- [ ] Docker image build workflow 추가
- [ ] 배포 자동화 방식 결정

## 지금 당장 추천하는 다음 작업

가장 먼저 할 작업은 테스트 기반을 만드는 것입니다.

1. `AccountBalance` 단위 테스트
2. `OrderService` 중복 주문 테스트
3. 잔고 부족 실패 테스트
4. API validation 테스트

이 네 가지가 잡히면 이후 체결, outbox publisher, 동시성 실험을 더 자신 있게 확장할 수 있습니다.
