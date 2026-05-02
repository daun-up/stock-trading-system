# Setup History

이 문서는 현재 저장소를 초기 세팅하면서 반영한 내용을 빠르게 확인하기 위한 기록입니다.

## 1. 원격 저장소 연결

- 로컬 Git 저장소를 GitHub 원격 저장소 `origin`에 연결했습니다.
- 연결된 주소는 `https://github.com/daun-up/stock-trading-system.git` 입니다.

## 2. 빌드 도구 전환

- 프로젝트 빌드 도구를 `Maven`에서 `Gradle`로 전환했습니다.
- 삭제한 파일:
  - `pom.xml`
- 추가한 파일:
  - `build.gradle`
  - `settings.gradle`
- 유지한 주요 설정:
  - Spring Boot `3.5.0`
  - Java `21` toolchain
  - Web, Validation, JPA, Actuator, Flyway, Kafka, PostgreSQL 의존성

## 3. Gradle 로컬 환경 세팅

- 로컬 macOS 환경에 `Homebrew`로 `Gradle 9.5.0`을 설치했습니다.
- 프로젝트에서 버전 고정을 위해 Gradle wrapper를 생성했습니다.
- 추가된 wrapper 파일:
  - `gradlew`
  - `gradlew.bat`
  - `gradle/wrapper/gradle-wrapper.jar`
  - `gradle/wrapper/gradle-wrapper.properties`

## 4. 현재 실행 방법

인프라 실행:

```bash
docker compose up -d
```

애플리케이션 실행:

```bash
./gradlew bootRun
```

테스트 실행:

```bash
./gradlew test
```

빌드 실행:

```bash
./gradlew build
```

## 5. 커밋 규칙

앞으로 기본 커밋 메시지는 가장 널리 쓰이는 `Conventional Commits` 스타일을 바탕으로 한글로 작성합니다.

예시:

- `feat: 주문 접수 API 추가`
- `fix: 잔고 차감 검증 오류 수정`
- `refactor: 주문 서비스 구조 정리`
- `docs: 실행 방법 문서화`
- `chore: Gradle wrapper 추가`

## 6. 반영된 최근 커밋

- `07b5c35` `Migrate build from Maven to Gradle`
- `13e2cd0` `chore: Gradle wrapper 추가`

## 7. 참고

- 현재 기본 브랜치는 `master`입니다.
- 원격 `origin/master`까지 푸시된 상태입니다.
