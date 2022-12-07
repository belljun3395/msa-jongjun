# 1인 프로젝트 목표

제가 선택한 1인 프로젝트 주제는 “**PROJECT 2 : 인증 시스템”** 입니다.

인증 시스템에세 제가 구현할 **우선 요구사항**은 다음과 같습니다.

- 가입, 로그인 페이지
- 유저 관리 페이지
- 인증 서버 API
    - 가입
        - 어드민 롤 부여
    - 로그인
        - 단일 기기 접속 판단 ← **개인적 추가 요구 사항**
    - 어드민
        - 그룹 생성
        - 그룹 참여 허용, 취소
        - 그룹 삭제
- RDBMS 사용
- Password Encryption

이 요구사항을 구현하기 위한 아키텍처는 다음과 같습니다.

![아키텍처](https://user-images.githubusercontent.com/102807742/206082197-6c972d91-a370-4f30-bdfa-76d5fe05d1e2.jpg)

우선 지금까지 경험한 프로젝트가 단순히 API를 만드는 것에 그쳤기 때문에 **MSA를 이해하는 것**에 조금 더 초점을 맞추었습니다.

그렇기에 **Netflix Zuul**과 같은 프레임 워크를 사용하여 API gateway를

그리고 **Netflix Feign**을 통해  loadblancer를 구현할 것입니다.

(Fegin은 **Hystrix**과 **Ribbon** 그리고 **Eureka**를 포함하고 있습니다.)

하지만 Netflix가 이제 더 이상 이들에 대한 개발을 진행하지 않고 있기 때문에 우선 Netflix를 통해 구현한 이후 다음과 같이 대체 할 예정입니다.

**Hystrix** → **Resilience4j**

**Ribbon → Spring Cloud Loadbalancer**

**Zuul → Spring Cloud GateWay**

그리고 Auth 그리고 API의 경우 **Spring Boot**를 활용하여 API를 만들 것입니다.

DB의 경우 AuthDB, API DB 그리고 Token DB로 구성되어 있으며 앞의 두개는 **MySql**을 그리고 나마저 하나는 **Redis**를 사용할 것입니다.

이는 **JWT** Refresh, Access Token을 활용하여 로그인을 구현할 것이기에 위와 같은 DB를 설계하였습니다.

또 우선은 API 서버간의 통신에도 Fegin을 사용하였는데 이 역시 구현 이후 **Kafka**와 같은 메시지 시스템으로 대체할 예정입니다.

### 프로젝트 목표를 우선 순위순으로 정리하면 다음과 같습니다.

1. 우선 프레임크를 활용하여 **MSA**를 이해한다.
2. Netflix에 의존한 의존성을 **Spring Cloud** 기반으로 대체한다.
3. **Kafka**를 도입해 메시지 시스템 구축한다.