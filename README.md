<h1 align="center" style="font-size:2.5em; font-weight:bold; margin:1em 0;">Paynalty (페이널티)</h1>

<p align="center">
  <strong>"실패는 중단이 아니라, 다음 도약을 위한 비용 이벤트입니다."</strong>
</p>

<p align="center">
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen?style=flat-square&logo=springboot" alt="Spring Boot"></a>
  <a href="https://reactnative.dev/"><img src="https://img.shields.io/badge/React%20Native-Latest-blue?style=flat-square&logo=react" alt="React Native"></a>
  <a href="https://openjdk.org/projects/jdk/21/"><img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" alt="Java 21"></a>
  <a href="https://developers-apps-in-toss.toss.im/"><img src="https://img.shields.io/badge/Platform-AppsInToss-004FE0?style=flat-square" alt="AppsInToss"></a>
  <img src="https://img.shields.io/badge/Security-mTLS-blueviolet?style=flat-square" alt="mTLS">
</p>

---

### 📌 Project Overview

**Paynalty**는 사용자가 목표를 달성하지 못했을 때 사전에 약속한 벌금을 부과함으로써 강력한 동기부여를 제공하는 **AppsInToss 기반의 목표 관리 Service**입니다. 

기존의 단순 기록형 서비스들과 달리, Paynalty는 **'실패의 비용 이벤트화'**라는 철학을 바탕으로 토스의 금융 인프라와 긴밀히 결합되어 있습니다. **mTLS 기반의 고도화된 보안 통신**을 통해 실제 결제 및 송금 Flow를 안전하게 처리하며, 사용자의 행동 변화를 실질적으로 이끌어냅니다.

---

### 📱 UI Preview

| 챌린지 생성 | 인증 및 대시보드 |
| :---: | :---: |
| <img src="./docs/img/screenshot_1.png" width="300" alt="Challenge Creation"> | <img src="./docs/img/screenshot_2.png" width="300" alt="Dashboard"> |
| **벌금 납부 내역** | **그룹 랭킹** |
| <img src="./docs/img/screenshot_3.png" width="300" alt="Penalty History"> | <img src="./docs/img/screenshot_4.png" width="300" alt="Group Ranking"> |

> [!TIP]
> 서비스의 실제 구동 화면은 `./docs/img/` 경로에 스크린샷 파일을 추가하여 확인할 수 있습니다.

---

### 🛠 Product Stack & Status

| Category | Technology & Description |
| :--- | : :--- |
| **Backend** | Java 21, Spring Boot 3.5.x, Spring Security, JPA |
| **Frontend** | React Native (TypeScript), Axios, Toss Design System (TDS) |
| **Security** | **mTLS (Mutual TLS)** integration, JWT, OAuth 2.0 |
| **Database** | MySQL (Production), H2 (Development) |

---

### 🚀 Key Technical Highlights

#### 1. Skin in the Game: 강력한 행동 제약
단순한 알림 보단 **실제 금융 비용(Penalty)**과 연결하여 사용자가 목표를 포기할 수 없는 환경을 조성합니다. 실패는 기록의 파편화가 아닌 정산의 Trigger이며, 모든 내역은 투명하게 로그로 남습니다.

#### 2. mTLS 기반의 Fintech 보안 구현
AppsInToss 플랫폼과의 통신 시, Client와 Server가 상호 인증하는 **mTLS(Mutual TLS)**를 적용하였습니다. 이는 높은 수준의 보안이 요구되는 금융권 API 연동 표준을 준수하며, 프로젝트의 기술적 완성도를 뒷받침합니다.

#### 3. Embedded Mini-app 기반의 심리스한 UX
별도의 앱 설치 없이 토스 앱 내에서 즉시 구동되는 **WebView 기반 Mini-app** 형태로 개발되었습니다. 토스 사용자의 접근성을 극대화하고, 익숙한 UI를 통해 서비스 몰입감을 높였습니다.

#### 4. 데이터 기반의 실패 분석 (Failure Analytics)
모든 실패와 벌금 발생 내역을 데이터화하여 시각화합니다. 사용자는 자신의 실패 패턴을 파악하고, 이를 바탕으로 더 현실적이고 달성 가능한 목표를 재설계할 수 있습니다.

---

### 🏗 Architecture & Domain Model

Paynalty는 유지보수와 확장을 고려하여 **Domain-Driven Design (DDD)** 지향의 구조를 가집니다.

| Component | Responsibility & Description |
| :--- | :--- |
| **Challenge** | 목표 정의, 인증 규칙, Penalty 정책 등을 관리하는 핵심 도메인 |
| **Penalty** | 실패 이벤트 발생 시 생성되는 벌금 데이터를 처리하며, 결제 상태와 연계 |
| **Toss Integration** | mTLS Handshake, Token Exchange 등 토스 플랫폼 연동을 추상화한 Module |
| **User** | 토스 OAuth를 통한 사용자 식별 및 Profile 정보 관리 |

---

### 💻 Getting Started

#### Prerequisites
- **JDK 21** & **Node.js 18+**
- AppsInToss Console에서 발급받은 **mTLS 인증서 (.pem)**

#### Backend Setup
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Frontend Setup
```bash
cd front
npm install
npm run start
```

---

### 📄 License
본 프로젝트는 팀 Paynalty의 포트폴리오 프로젝트로, 무단 복제 및 전재를 금합니다. 

---
<p align="right"><a href="#paynalty-페이널티">↑ Back to Top</a></p>


