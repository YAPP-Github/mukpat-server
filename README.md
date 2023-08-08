# 🍖 먹팟 - 직장인 랜덤 점심 모임 서비스 
![A3](https://github.com/YAPP-Github/mukpat-client/assets/51940808/4c268fd2-d239-4a50-9877-a2d475ea58ca)
 
<br/>

> **먹팟**은 같이 먹을 사람을 고민하거나, 새로운 사람을 만나고 싶은 유저들의 네트워킹을 위한 **식사 모임**을 도와주는 서비스입니다. <br/> 
> 공통의 관심사나 취미, 취향을 가진 사람들을 모집할 수 있도록 먹팟을 올리고 동료들을 모을 수 있습니다.   

<br/>  

## 📋 기능 설명

1️⃣ 내 주변 먹팟 찾기  
- 지역 필터를 이용해 쉽게 내 주변 먹팟을 찾을 수 있어요. 

2️⃣ 먹팟 만들기  
- 내가 원하는 다양한 조건으로 먹팟을 등록할 수 있어요.    
  
3️⃣ 먹팟 참여하기  
- 원하는 먹팟을 찾았다면? '참여하기'를 누르고 멤버들을 만나요.

<br/> 

## ⚡️ 아키텍처
![image](https://github.com/YAPP-Github/mukpat-server/assets/67696767/a79e000d-6e12-4ea4-89cf-8e67413f305c)

## ⚡️ 인프라
![image](https://github.com/YAPP-Github/mukpat-server/assets/67696767/2ecbc514-12c0-4c4b-9e47-6dd93ebd4552)

## ⚡️ CI/CD
![image](https://github.com/YAPP-Github/mukpat-server/assets/67696767/7d77ed37-ccb8-41e5-a4e3-8f79d6ce934c)


## 📁 프로젝트 구조
멀티모듈 구조 사용했습니다.

### 1. api 모듈

```text
- 비즈니스 로직을 담당하는 모듈
- contoller+service 계층
```

|             | api | domain | infra | 
|-------------|---------|---------|---------|
| 사용가능한 모듈 여부 | -       | O       | O       |

### 2. domain 모듈

```text
- 비즈니스 로직에서 사용하는 도메인 객체를 다루는 모듈 
- repository 계층
```
|             | api | domain | infra | 
|-------------|---------|---------|---------|
| 사용가능한 모듈 여부 | -       | -       | O       |

### 3. infra 모듈

```text
- 외부 서비스 기능 관리하는 모듈
- AWS SES, redis
```

|             | api | domain | infra | 
|-------------|---------|---------|---------|
| 사용가능한 모듈 여부 | -       | -       | -       |

```bash
├── muckpot-api
│   └── src
│       ├── main
│       │   ├── kotlin
│       │   │   └── com
│       │   │       └── yapp
│       │   │           └── muckpot
│       │   │               ├── common
│       │   │               ├── config
│       │   │               ├── domains
│       │   │               │   ├── user
│       │   │               │       ├── controller
│       │   │               │       └── service
│       │   │               │   └── ...
│       │   │               ├── exception
│       │   │               └── filter
│       │   └── resources
│       └── test
├── muckpot-domain
│   └── src
│       ├── main
│       │   ├── kotlin
│       │   │   └── com
│       │   │       └── yapp
│       │   │           └── muckpot
│       │   │               ├── common
│       │   │               ├── config
│       │   │               └── domains
│       │   │                   ├── user
│       │   │                       ├── entity
│       │   │                       ├── enums
│       │   │                       ├── exception
│       │   │                       └── repository
│       │   │                   └── ...
│       │   └── resources
│       ├── test
│       └── testFixtures
└── muckpot-infra
    └── src
        ├── main
        │   ├── kotlin
        │   │   └── com
        │   │       └── yapp
        │   │           └── muckpot
        │   │               ├── email
        │   │               └── redis
        │   └── resources
        ├── test
        └── testFixtures
```

<br/>

## 👩🏻‍💻 백엔드 팀원 소개

<table>
    <tr align="center">
        <td><B>Backend</B></td>
        <td><B>Backend</B></td>
    </tr>
    <tr align="center">
        <td><B>강태산</B></td>
        <td><B>이채린</B></td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/Pawer0223.png?size=100" width="100">
            <br>
            <a href="https://github.com/Pawer0223"><I>Pawer0223</I></a>
        </td>
        <td>
            <img src="https://github.com/cofls6581.png?size=100" width="100">
            <br>
            <a href="https://github.com/cofls6581"><I>cofls6581</I></a>
        </td>
    </tr>
</table>
