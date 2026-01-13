<h1 align="center" style="font-size:2.5em; font-weight:bold; margin:1em 0;">Paynalty (페이널티)</h1>

<!-- <p align="center">
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen?style=flat-square&logo=springboot" alt="Spring Boot"></a>
  <a href="https://reactnative.dev/"><img src="https://img.shields.io/badge/React%20Native-Latest-blue?style=flat-square&logo=react" alt="React Native"></a>
  <a href="https://openjdk.org/projects/jdk/21/"><img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" alt="Java 21"></a>
  <a href="https://developers-apps-in-toss.toss.im/"><img src="https://img.shields.io/badge/Platform-AppsInToss-004FE0?style=flat-square" alt="AppsInToss"></a>
  <img src="https://img.shields.io/badge/Security-mTLS-blueviolet?style=flat-square" alt="mTLS">
</p> -->

<img src="./docs/img/paynalty_readmeImg.png" alt="readmeImg">

---

### 📱 UI Preview

|                                  메인 화면                                   |                             오늘의 미션                              |
|:------------------------------------------------------------------------:|:---------------------------------------------------------------:|
| <img src="./docs/img/preview2.png" width="300" alt="Challenge Creation"> | <img src="./docs/img/preview1.png" width="300" alt="Dashboard"> |

---

### 🛠 Tech Stack

![2.png](docs/img/2.png)

---

### 🚀 Workflow

### FRONT

#### 상태 관리 전략

![3.png](docs/img/3.png)

#### 인증 및 데이터 동기화 / 챌린지 삭제

![4.png](docs/img/4.png)

### BACKEND

#### 토스 로그인

![5.png](docs/img/5.png)

#### 챌린지 생성

![6.png](docs/img/6.png)

#### 인증 생성

![7.png](docs/img/7.png)

#### window generator

![8.png](docs/img/8.png)

#### window enforcer

![9.png](docs/img/9.png)
---

### 시연 영상

|**온보딩, 메인, 로그인**|**챌린지 생성**|
|:----------------------------------------:|:-----------------------------------------------:|
| ![온보딩페이지, 메인페이지, 로그인.gif](docs/img/%E1%84%8B%E1%85%A9%E1%86%AB%E1%84%87%E1%85%A9%E1%84%83%E1%85%B5%E1%86%BC%E1%84%91%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%B5%2C%20%E1%84%86%E1%85%A6%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%91%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%B5%2C%20%E1%84%85%E1%85%A9%E1%84%80%E1%85%B3%E1%84%8B%E1%85%B5%E1%86%AB.gif) | ![챌린지 생성.gif](docs/img/%E1%84%8E%E1%85%A2%E1%86%AF%E1%84%85%E1%85%B5%E1%86%AB%E1%84%8C%E1%85%B5%20%E1%84%89%E1%85%A2%E1%86%BC%E1%84%89%E1%85%A5%E1%86%BC.gif) |
|**친구 초대**|**챌린지 디테일/벌금, 인증 내역, 수정 삭제**|
|![친구 초대.gif](docs/img/%E1%84%8E%E1%85%B5%E1%86%AB%E1%84%80%E1%85%AE%20%E1%84%8E%E1%85%A9%E1%84%83%E1%85%A2.gif)|![챌린지 디테일.gif](docs/img/%E1%84%8E%E1%85%A2%E1%86%AF%E1%84%85%E1%85%B5%E1%86%AB%E1%84%8C%E1%85%B5%20%E1%84%83%E1%85%B5%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%86%AF.gif)|
|**인증 하기**||
|![인증.gif](docs/img/%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%8C%E1%85%B3%E1%86%BC.gif)||

---

### 💻 Getting Started

#### Prerequisites

- **JDK 21**
- AppsInToss Console에서 발급받은 **mTLS 인증서 (.pem)**
- 환경변수 파일 .env
- 샌드박스 APP

#### Frontend Setup

```bash
cd front
npm install
npm run dev
```

---