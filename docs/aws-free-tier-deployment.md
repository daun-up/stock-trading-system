# AWS Free Tier Deployment Plan

이 문서는 이 프로젝트를 AWS 프리티어 범위에서 학습용으로 배포할 때의 기준을 정리합니다.

## 추천 1차 목표

처음 목표는 아래 정도로 제한합니다.

```text
Internet
  -> EC2 Spring Boot app
  -> RDS PostgreSQL
```

Redis와 Kafka는 로컬 개발 환경에 남겨두고, 실제 기능이 붙은 뒤 AWS에 올립니다. 처음부터 ElastiCache, MSK까지 붙이면 비용과 설정 복잡도가 빠르게 커집니다.

## 왜 EC2와 RDS인가

- EC2는 작은 서버 하나에 Docker 컨테이너를 올려 Spring Boot 앱을 실행하기 쉽습니다.
- RDS PostgreSQL은 DB 설치, 백업, 패치 같은 운영 부담을 줄여줍니다.
- 앱 서버와 DB를 분리하면 GitHub나 서버 이미지 안에 DB 파일을 넣지 않아도 됩니다.

## GitHub에 올리면 안 되는 것

아래 값은 절대 커밋하지 않습니다.

- AWS Access Key ID
- AWS Secret Access Key
- RDS password
- API key
- JWT secret
- OAuth client secret
- `.env`
- `.pem`, `.key`, `.p12`, `.jks`
- 실제 운영 도메인에 연결된 인증서 개인키

저장소에는 `.env.example`처럼 예시 이름과 placeholder만 둡니다.

## 운영 설정 방식

운영 환경에서는 `application-prod.yml`이 환경변수를 읽습니다.

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/stock_trading_system
SPRING_DATASOURCE_USERNAME=<db-user>
SPRING_DATASOURCE_PASSWORD=<db-password>
```

이 값들은 GitHub가 아니라 EC2 환경변수, systemd EnvironmentFile, AWS Systems Manager Parameter Store, 또는 Secrets Manager에 둡니다.

## EC2에서의 간단한 실행 형태

```bash
docker build -t stock-trading-system .

docker run -d \
  --name stock-trading-system \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<rds-endpoint>:5432/stock_trading_system" \
  -e SPRING_DATASOURCE_USERNAME="<db-user>" \
  -e SPRING_DATASOURCE_PASSWORD="<db-password>" \
  stock-trading-system
```

운영 비밀번호를 위 명령어에 직접 남기는 방식은 학습 초반에만 사용하고, 이후에는 `--env-file` 또는 AWS Secrets Manager/SSM Parameter Store로 옮깁니다. 단, 실제 `.env` 파일은 GitHub에 올리지 않습니다.

## 비용 주의

- AWS 프리티어 조건은 계정 생성일과 리전에 따라 달라질 수 있습니다.
- RDS, EC2, EBS, 스냅샷, 데이터 전송량은 각각 따로 과금될 수 있습니다.
- Budget 알림을 먼저 만들고, 월 비용 알림을 낮은 금액으로 설정합니다.
- 실습이 끝난 리소스는 중지 또는 삭제합니다. 특히 RDS 스냅샷과 EBS 볼륨이 남아 있는지 확인합니다.
