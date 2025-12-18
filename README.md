## Penalty (페이널티)

**“목표를 못 지키면 돈을 내고, 실패는 기록으로 남기는 토스 미니앱 기반 목표 관리 서비스”**

4인 팀이 개발하는 프로젝트로,  
**백엔드(Spring Boot, Java)** + **프론트(React Native, 토스 앱인토스 미니앱)** 구조입니다.

---

## 1. 서비스 소개

- 챌린지를 만들고 매일 인증합니다.
- 실패할 때마다 **사전에 정한 벌금이 발생**합니다.
- 모인 벌금은 **토스 송금/보관 흐름**과 연결됩니다.
- 실패는 **중단이 아니라 비용 이벤트**이며, 모든 실패/성공 기록이 남습니다.

핵심 개념:

- **mTLS 기반 토스 AppsInToss 연동**
- **그룹 챌린지 / 벌금 관리 / 통계 시각화**

---

## 2. 프로젝트 구조

```text
Paynalty/
├── backend/             # Spring Boot (Java 21)
│   ├── src/main/java/com/paynalty
│   │   ├── domain/
│   │   │   ├── challenge/ ...
│   │   │   ├── penalty/           # 벌금 도메인
│   │   │   └── toss/              # 토스 연동 (mTLS, 테스트 컨트롤러 등)
│   │   └── global/
│   │       ├── config/            # Security, CORS, TossApiConfig(mTLS)
│   │       └── error/             # 공통 에러 코드/핸들러
│   └── src/main/resources/
│       ├── application.yml        # 공통 설정 (토스 설정 포함)
│       ├── application-dev.yml    # 개발용 DB 설정
│       └── application-prod.yml   # 운영용 DB 설정
└── frontend/            # React Native (토스 미니앱용 클라이언트)
    └── src/
        ├── navigation/
        ├── services/api/          # 백엔드 연동 클라이언트
        └── types/
```

---

## 3. 개발 환경

- **OS**: Windows 10 (팀원 기준)
- **Backend**
  - Java 21
  - Spring Boot 3.5.x
  - Gradle (Wrapper 사용)
  - DB: H2 (dev), MySQL (prod)
- **Frontend**
  - React Native
  - TypeScript
  - Axios 기반 API 클라이언트

---

## 4. 백엔드 실행 방법 (개발용)

### 4-1. 사전 준비

1. **JDK 21 설치**
2. **환경 변수 설정 (선택)**  
   `.env` 파일 대신 OS 환경 변수로 관리:

```powershell
# 예시 (PowerShell)
$env:SPRING_PROFILES_ACTIVE="dev"

# 토스 연동 관련 (나중에 실제 값으로 교체)
$env:TOSS_API_BASE_URL="https://apps-in-toss.toss.im"
$env:TOSS_CLIENT_ID="your-client-id"
$env:TOSS_CLIENT_SECRET="your-client-secret"
```

### 4-2. 빌드 & 실행

```bash
cd backend
./gradlew clean build
./gradlew bootRun
```

## 5. 토스 mTLS 설정 요약

백엔드는 **AppsInToss API 호출 시 mTLS(mutual TLS)** 를 사용합니다.

### 5-1. 필요한 파일

앱인토스 콘솔에서 **서버 mTLS 인증서** 발급 후, 받은 파일을 기준으로:

- `client-cert_public.crt` → `backend/src/main/resources/certs/client-cert.pem`
- `client-cert_private.key` → `backend/src/main/resources/certs/client-key.pem`

> 현재 코드는 **클라이언트 인증서 + 개인키만 사용**하고,  
> TrustStore는 JDK 기본값을 사용하도록 단순화되어 있습니다.

### 5-2. 설정 파일

`backend/src/main/resources/application.yml`:

```yaml
toss:
  api:
    base-url: ${TOSS_API_BASE_URL:https://api.toss.im}
    client-id: ${TOSS_CLIENT_ID:}
    client-secret: ${TOSS_CLIENT_SECRET:}
    # mTLS 인증서 경로 (resources/certs 폴더 기준)
    mtls:
      client-cert-path: ${TOSS_CLIENT_CERT_PATH:classpath:certs/client-cert.pem}
      client-key-path: ${TOSS_CLIENT_KEY_PATH:classpath:certs/client-key.pem}
      # 인증서 비밀번호 (있는 경우만)
      key-password: ${TOSS_KEY_PASSWORD:}
```

### 5-3. mTLS 설정 코드 위치

- `backend/src/main/java/com/paynalty/global/config/TossApiConfig.java`
  - 클라이언트 인증서/키 로딩
  - `SSLContext` 생성
  - mTLS 적용 `RestTemplate` Bean (`tossRestTemplate`) 제공

### 5-4. 토스 로그인 테스트 엔드포인트

테스트용 컨트롤러:

- `backend/src/main/java/com/paynalty/domain/toss/test/TossTestController.java`

엔드포인트:

- `GET /api/toss/login-test?code={authorizationCode}`
  - 프론트(미니앱)에서 AppsInToss `appLogin()` 호출로 받은 `authorizationCode`를 넘기면,
  - 백엔드가 토스 OAuth 토큰 교환 API를 호출하면서 **mTLS가 실제로 동작하는지 테스트**할 수 있습니다.

> 실제 AppsInToss 로그인/결제 플로우는 토스 공식 문서(`TOSS_full`)를 기준으로 프론트에서 `appLogin` → 백엔드에서 토큰 교환/영수증 검증 순서로 구현합니다.

---

## 6. 주요 도메인 개요

- `challenge` : 챌린지(목표) 정의, 벌금 금액, 주기 등
- `penalty` : 실패 시 발생하는 벌금 기록 (사용자, 챌린지, 금액, 결제 상태 등)
- `toss` : 토스 로그인/결제 연동 DTO & 서비스
- `user` : 사용자 정보

---

## 7. 프론트엔드 개요

- React Native 기반 앱 (토스 미니앱 컨셉)
- API 클라이언트: `frontend/src/services/api/`
  - `client.ts` : Axios 인스턴스 (Base URL, 공통 인터셉터)
  - `penaltyApi.ts`, `paymentApi.ts`, `userApi.ts` 등 도메인별 래퍼
- 타입 정의: `frontend/src/types/`

프론트 개발/실행 방법은 추후 정리 예정 (예: `npm install`, `npm run android/ios` 등).

---

## 8. 팀 개발 규칙 (초안)

- **브랜치 전략**

  - `main`: 배포/데모용
  - `dev`: 통합 개발 브랜치
  - 기능 단위 브랜치: `feature/도메인-기능명` (예: `feature/penalty-payment`)

- **커밋 메시지 컨벤션 (예시)**

  - `feat: ~` 새로운 기능
  - `fix: ~` 버그 수정
  - `chore: ~` 설정/빌드/기타
  - `docs: ~` 문서 수정 (README 등)

- **코딩 컨벤션**
  - Java: Spring 기본 컨벤션 + Lombok 활용
  - TypeScript: ESLint/Prettier 설정 기준

---

## 9. 앞으로 할 일 (로드맵 초안)

- [ ] AppsInToss `appLogin` 연동 (프론트)
- [ ] 토스 결제/영수증 검증 API 정식 연동
- [ ] 벌금 결제 플로우 (`Penalty` + `Payment`) 설계 및 구현
- [ ] 그룹 챌린지, 대시보드, 통계 API/화면 구현
- [ ] 운영 환경 mTLS/DB/로깅 설정 정리

---

## 10. 문의

프로젝트 관련 질문은 팀 내 Slack/Notion 또는 이 저장소 이슈로 공유합니다.
