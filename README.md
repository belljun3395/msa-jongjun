# Smile-gate WinterDev 1인 프로젝트  김종준 - 인증서버 구현

## 역할 및 기능
| 서비스       | 역할 및 기능                                                                                                                                                 |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| zuul 서버   | - API Gateway로 사용자의 요청에 따라 알맞은 서버에 요청을 전달 <br/> - 사용자 요청에 대한 로그 저장 <br/> - 사용자 요청에 따른 사전 요구 사항 수행                                                       |
| eureka 서버 | - API Gateway로 가동되고 있는 서버에 대한 정보 전달                                                                                                                     |
| auth 서버   | - 사용자 회원가입, 로그인, 로그아웃, 권한 조정 그리고 이메일 인증과 같이 사용자에 관한 요청 담당                                                                                               |
| group 서버  | - 그룹에 관련된 요청 담당<br/> &nbsp;&nbsp;&nbsp;&nbsp;- Admin 권한 : 그룹 생성, 삭제, 이름 변경, 최대 인원 변경, 주인 변경, 그룹원 추방<br/> &nbsp;&nbsp;&nbsp;&nbsp;-Member 권한 : 그룹 참여, 탈퇴 |
| email 서버  | - API의 요청에 따른 email 전송 담당                                                                                                                               |


## 기술 스택
+ Java 11
+ Springboot 2.1.0.RELEASE
+ Spring Cloud Starter Netflix Zuul
+ Spring Cloud Netflix Eureka
+ Spring Cloud Starter Open Feign
+ Springboot Starter mail
+ Jwt
+ Mysql
+ redis
+ kafka

## 아키텍처
![스마일게이트 인증서버 drawio](https://user-images.githubusercontent.com/102807742/209357135-82b73f83-5ed5-47c7-a9d2-970c94d86224.png)

## 주요 구현 및 질문
### zuul
#### ZuulPreLogginFilter
```java
@Override
public boolean shouldFilter() {
   RequestContext context = RequestContext.getCurrentContext();
   HttpServletRequest request = context.getRequest();
   String uri = request.getRequestURI();
   if (uri.matches("/auth/members/.*")) {
   return true;
   }

    String accessToken = request.getHeader(AUTHORIZATION_HEADER);
    if (!token.validateAccessToken(accessToken)) {
        throw new NotValidateTokenExceptionCustom();
    }
    return true;
}
```
로그를 남기기 전에 로그인 요청을 제외한 다른 요청들에 대해서는 accessToken을 확인하는 먼저 `shouldFilter`를 먼저 지나간다.
```java
@Override
public Object run() throws ZuulException {
    String uuid = UUID.randomUUID().toString();
    RequestContext context = RequestContext.getCurrentContext();
    context.set("uuid", uuid);
    HttpServletRequest request = context.getRequest();
    String requestURI = request.getRequestURI();
    log.info("REQUEST [{}][{}]", uuid, requestURI);
    return null;
}
```
이후 `run`을 통해 위와 같이 로그를 남겨준다.

`ZuulPostLogginFilter`의 `run` 역시 위와 같이 구성한다.

토큰 유효성 확인의 경우 다음과 같이 진행한다.
```java
@FeignClient(value = "auth", fallbackFactory = FeignValidateAccessTokenFallbackFactory.class)
public interface FeignValidateAccessToken {

    @RequestMapping(path = "/tokens/validation")
    boolean validateAccessToken(@RequestHeader("Authorization") String token);

}
```

---
`Question 1`

위와 같이 코드를 구성하면 auth 서버에 access 토큰을 확인해야하는 모든 요청에 대해서 확인을 하게 됩니다.

저의 경우 zuul 서버와 auth 서버가 따로 구동되고 있는데 이러한 경우에도 위와 같이 요청을 매번 보내도 괜찮을지 궁금합니다.

괜찮을지에 대해서 구체적으로 서술하자면 feign은 restTemplate와 같이 http 요청을 보내주는 것으로 알고 있습니다.

그렇기에 위와 같이 access 토큰 확인이 필요한 매 요청마다 http 요청을 보내는 것이 괜찮은지 궁금합니다.

이후에 role에 대한 확인을 하는 것도 access 토큰 검증과 같은 과정을 거치기에 auth 서버는 api gateway 서버와 합치는 것이 더 좋은 선택일지도 궁금합니다.

--- 

#### ZuulPreRoleFilter
```java
@Override
public boolean shouldFilter() {
    RequestContext context = RequestContext.getCurrentContext();
    HttpServletRequest request = context.getRequest();
    String uri = request.getRequestURI();
    String accessToken = request.getHeader(AUTHORIZATION_HEADER);
    if (uri.contains("admin")) {
        context.set("role", "admin");
        if (!token.validateAccessTokenRole(accessToken, "admin")) {
            throw new NotAllowedAPIExceptionCustom();
        }
        return true;
    }
    return true;
}
```

### auth
#### login
```java
// MemberServiceImpl

@Override
@Transactional
public TokenDTO login(MemberLoginDTO memberLoginDTO) {
    String email = memberLoginDTO.getEmail();
    String password = memberLoginDTO.getPassword();
    Member member = findMemberBy(email);

    validatePassword(password, member);

    MemberLoginInfo memberLoginInfo = makeMemberLoginInfo(member, memberLoginDTO);
    applicationEventPublisher.publishEvent(new MemberLoginEvent(memberLoginInfo));

    return new TokenDTO(memberLoginInfo.getAccessToken(), memberLoginInfo.getRefreshToken());
}
```
login의 경우 loginLog를 남기기 의해 `MemberLoginEvent`를 발생시킵니다.

`MemberLoginEvent` 및 `MemberLoginEventHandler`는 다음과 같습니다.

```java
public class MemberLoginEvent extends ApplicationEvent {

    private MemberLoginInfo memberLoginInfo;

    public MemberLoginEvent(MemberLoginInfo memberLoginInfo) {
        super(memberLoginInfo);
        this.memberLoginInfo = memberLoginInfo;
    }
}

```
```java
public class MemberLoginEventHandler {

    private final LoginLogRepository repository;
    private final AccessTokenRepository accessTokenRepository;

    @Async
    @EventListener
    @Transactional
    public void handle(MemberLoginEvent event) {
        MemberLoginInfo memberLoginInfo = event.getMemberLoginInfo();
        repository.save(loginLogFrom(memberLoginInfo));
        accessTokenRepository.save(accessTokenFrom(memberLoginInfo));
    }
}
```
이벤트를 통해 이들을 처리한 이유는 `MemberLoginEventHandler`를 보면 `MemberRepository`가 아닌 `LoginLogRepository` 그리고 `AccessTokenRepository`를 사용하고 있는 것을 볼 수 있습니다.

`MemberRepository`가 아닌 다른 Repository와 연관관계를 만들고 싶지 않아 위와 같이 처리하였습니다.

--- 
`Question 2`

`MemberLoginEventHandler`의 `handle`에서는 `LoginLogRepository` 그리고 `AccessTokenRepository` 2곳에 저장을 하고 있습니다.

이 경우 transaction의 원자성을 어떻게 지킬 수 있을지 궁금합니다.

--- 

#### token expireTime refresh
```java
// AccessTokenServiceImpl

@Transactional
public boolean validateAccessToken(String accessTokenValue) {
    try {
        if (JwtToken.validateExpirationTime(accessTokenValue)) {
            AccessToken accessToken = getAccessTokenBy(JwtToken.getUUID(accessTokenValue));
            accessToken.refreshExpiredTime();
            save(accessToken);
        }
        return true;
    } catch (JwtException | RedisException e) {
        return false;
    }
}
```

저의 경우 token의 expireTime 갱신을 위와 같이 진행하였습니다.

token을 조회하고 그 토큰의 expireTime 그리고 redis에 저장될 accessToken의 expiredTime을 갱신하여 주었습니다.

즉 토큰 값은 변화가 없고 expireTime만 변화가 있는 것 입니다.

---
`Question 3`

위처럼 expireTime만 변경시키고 token의 값은 유지하는 것이 괜찮은지 궁금합니다.

---

#### jwt Token

````java
// MemberLoginInfo

private String makeAccessToken() {
    return JwtToken.makeToken(ACCESS_TOKEN_EXP, this.makeUUID());
}

private String makeRefreshToken() {
    return JwtToken.makeToken(REFRESH_TOKEN_EXP, this.makeTokenInfo());
}

private Map<String, Object> makeTokenInfo() {
    Map<String, Object> tokenInfo = new HashMap<>();
    tokenInfo.put(MEMBER_ID, memberId);
    tokenInfo.put(ROLE, role);
    return tokenInfo;
}

private Map<String, Object> makeUUID() {
    HashMap<String, Object> uuidInfo = new HashMap<>();
    uuidInfo.put(UUID_KEY, UUID.randomUUID());
    return uuidInfo;
}
````

access 토큰에는 uuid를 그리고 refresh 토큰에는 memberId 그리고 role 정보를 넣었습니다.

이는 redis의 accessToken 정보에 다음과 같이 memberId, role에 관한 정보를 함께 기록하기 때문입니다.

```java
private AccessToken accessTokenFrom(MemberLoginInfo memberLoginInfo) {
    return new AccessToken(memberLoginInfo.getAccessToken(), memberLoginInfo.getMemberId(), memberLoginInfo.getRole());
}

```

--- 
`Question 4`

이는 jwt 토큰의 경우 payload가 공개된 정보이기 때문에 최대한 적은 정보를 담고 싶어 위와 같이 코드를 구성하였습니다.

jwt 토큰에 어느 정도의 정보가 들어가도 괜찮은지 궁금합니다.

---

#### email Auth
```java
// MemberServiceImpl

@Override
public String emailAuth(MemberAuthInfoDTO memberAuthInfoDTO) {

    String uuid = createKey();
    String key = createKey();
    memberAuthInfoDTO.setUuid(uuid);
    memberAuthInfoDTO.setKey(key);
    ListenableFuture<SendResult<String, MemberAuthInfoDTO>> emailAuth = memberAuthInfoDTOKafkaTemplate.send("emailAuth", memberAuthInfoDTO);

    emailAuth.addCallback(new ListenableFutureCallback<>() {
        @Override
        public void onSuccess(SendResult<String, MemberAuthInfoDTO> result) {
            System.out.println("Sent message=[" + memberAuthInfoDTO +
                    "] with offset=[" + result.getRecordMetadata()
                    .offset() + "]");
            authMemberInfoRepository.save(new AuthMemberInfo(uuid, key));
        }

        @Override
        public void onFailure(Throwable ex) {
            System.out.println("Unable to send message=["
                    + memberAuthInfoDTO + "] due to : " + ex.getMessage());
        }
    });

    return uuid;
}
```

이는 email 인증을 위해 uuid와 key와 함께 email 서버에 auth email을 보내달라는 요청을 kafka를 통해 하는 코드입니다.

---
`Question 5`

코드를 작성하고 난 이후에 이 경우에는 feign과 같이 '요청을 하는 것이 좋은가?' 아님 위에 구현한 것 처럼 'message, event를 발생시키는 것이 좋은가?' 하는 생각이 들었습니다.

제가 공부한 바로는 요청의 경우 return 값이 필요한 경우 그렇지 않은 경우는 event, message가 알맞다는 생각을 하였습니다.

kafka와 같은 툴은 언제 사용하면 좋은지 가이드가 필요합니다.

또 위의 kafka의 경우 가장 기본적인 설정만 하였는데 kafka를 사용할 때 주의해야할 점이 어떤 것이 있는지 궁금합니다.

---

### 공통
#### API Response & DTO
```java
public class MemberController {

    private final String AUTHORIZATION = "Authorization";
    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<ApiResponse.withCodeAndMessage> join(@Validated MemberJoinDTO memberJoinDTO) {
        memberService.join(memberJoinDTO);
        return ApiResponseGenerator.success(HttpStatus.OK, 1100, "join success");
    }

    @PostMapping("/login")
    public void login(@Validated MemberLoginDTO memberLoginDTO, HttpServletResponse response) {
        TokenDTO tokenDTO = memberService.login(memberLoginDTO);
        response.setHeader(AUTHORIZATION, tokenDTO.getAuthorizationToken());
        response.addCookie(tokenDTO.getTokenCookie());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        memberService.logout(token);
        removeCookieToken(response);
    }

    private static void removeCookieToken(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("refresh_token", null);
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
//        response.setHeader("Set-Cookie", null);
    }

    @PostMapping("/role")
    public void adjustRole(MemberRoleDTO memberRoleDTO) {
        memberService.adjustRole(memberRoleDTO.getMemberId(), Role.makeRole(memberRoleDTO.getRole()));
    }

    @PostMapping("/email")
    public String emailAuth(MemberAuthInfoDTO memberAuthInfoDTO) {
        return memberService.emailAuth(memberAuthInfoDTO);
    }

    @PostMapping("/email/key")
    public boolean validateKey(AuthKeyInfoDTO authKeyInfoDTO) {
        return memberService.validateAuthKey(authKeyInfoDTO);
    }
}
```

```java
public class TokenController {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final AccessTokenService service;

    @GetMapping("/members")
    public MemberInfoDTO browseMemberMatch(HttpServletResponse response, @RequestHeader("Authorization") String accessTokenValue) {
        response.setHeader(AUTHORIZATION_HEADER, accessTokenValue);
        return service.browseMemberMatch(accessTokenValue);
    }

    @GetMapping("/validation")
    public boolean validateAccessToken(@RequestHeader("Authorization") String accessTokenValue) {
        return service.validateAccessToken(accessTokenValue);
    }

    @GetMapping("/validation/role/{role}")
    public boolean validateAccessTokenRole(@RequestHeader("Authorization") String accessTokenValue, @PathVariable String role) {
        return service.validateAccessTokenRole(accessTokenValue, role);
    }
}
```

---
`Question 7`

위의 두 controller를 보면 값이 있는 경우 크게 ApiResponse 그리고 DTO로 반환하는 것을 볼 수 있습니다.

ApiResponse의 경우 
```java
{
  timeStamp : 타임스템프,
  code : API별 성공코드,
  message : 성공 메시지,
  data : { 데이터 }
}
```
위와 같이 구성 됩니다.

단순한 boolean 값을 반환할 경우나 void의 경우에도 위와 같이 통일하여 결과를 반환하여 주어야 할까요?

또 저는 Front에게 반환하는 정보는 ApiResponse를 통해 반환하고 아닌 경우 단순 DTO로 반환하였는데 이는 좋은 판단 인지 궁금합니다.

---
`Question 8`

그리고 DTO는 어떠한 계층까지 사용하는 것이 좋은지 궁금합니다.

지금 저의 경우 DTO를 통해 controller에서 service 계층으로 데이터를 이동 시키는데 이전에는 DTO를 controller 단에만 국한 시켰습니다.

DTO, 즉 data `Transfer` object 이기에 data를 이동시키는 것이 이해하지만 그 계층이 어디까지가 적절한지 궁금합니다.

___

#### HTTP Method
```java
public class GroupController {

    private final GroupService service;

    @PostMapping("/admin")
    public ApiResponse<ApiResponse.withCodeAndMessage> makeGroup(GroupDTO groupDTO) {
        service.makeGroup(groupDTO);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success make group");
    }


    @DeleteMapping("/admin")
    public ApiResponse<ApiResponse.withCodeAndMessage> deleteGroup(GroupOwnerInfoDTO groupOwnerInfoDTO) {
        service.deleteGroup(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId());
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success delete group");
    }

    @PostMapping("/admin/name")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupName(GroupOwnerInfoDTO groupOwnerInfoDTO, String groupName) {
        service.modifyGroupName(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), groupName);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify groupName");
    }

    @PutMapping("/admin/max")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupMaxMember(GroupOwnerInfoDTO groupOwnerInfoDTO, Integer maxMember) {
        service.modifyGroupMaxMember(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), maxMember);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify group max Member");
    }

    @PutMapping("/admin/owner")
    public ApiResponse<ApiResponse.withCodeAndMessage> modifyGroupOwner(GroupOwnerInfoDTO groupOwnerInfoDTO, Long newOwnerId) {
        service.modifyGroupOwner(groupOwnerInfoDTO.getGroupId(), groupOwnerInfoDTO.getOwnerId(), newOwnerId);
        return ApiResponseGenerator.success(HttpStatus.OK, 1300, "success modify group owner");
    }

}
```
---
`Question 8`

`GroupController`는 위의 코드처럼 HTTP Method를 다양하게 사용하여 구현하였습니다.

restful한 API를 구현하기 위한 조건 중 하나로 url에 동사가 들어가지 않는다는 조건이 있는 것으로 알고 있습니다.

그리고 최대한 Post와 Get Method만 사용하는 것으로 알고 있습니다.

그렇다면 Post와 Delete는 어떻게 구분할 수 있을지 url을 명령하는 팁이 궁금합니다.
___

## 추후계획
### 0. 공통 속성들 하나로 관리할 수 있도록 환경 구축하기
### 1. 현재 프로젝트에 대한 테스트 결과 남기기 

### 2. 개인 목표에 명시한 것처럼 Netflix 기반의 라이브러리를 다음과 같이 변경하는 것

+ Hystrix → Resilience4j

+ Ribbon → Spring Cloud Loadbalancer

+ Zuul → Spring Cloud GateWay

### 3. Kafka를 단순히 사용만하는 것이 아닌 이해하고 사용하기
