# 인증, 인가 유즈 케이스/분석

# 유즈 케이스

## 인증

사용자는 회원가입을 할 수 있다.

사용자는 로그인을 할 수 있다.

사용자는 비밀번호 찾기를 할 수 있다.(추후)

---

## 인가

사용자는 관리자 권한을 신청할 수 있다.

# 분석

## 인증

### 회원가입

회원가입은 아이디, 비밀번호, 이름을 통해 회원가입을 할 수 있다.

아이디는 이메일 형식으로 중복될 수 없다.

비밀번호는 우선 특정한 조건 없이 설정하겠지만 추후 특정 조건을 설정할 것이다.

비밀번호는 암호화하여 저장한다.

이름은 한국어만 입력 받는다.

### 로그인

로그인은 아이디와 비밀번호를 입력받아 진행한다.

아이디를 통해 회원DB에서 정보를 조회하여 비밀번호를 비교하여 일치하면 로그인을 승인한다.

로그인이 승인되면 RefreshToken과 기종 그리고 위치 정보를 기록한다.

그리고 AccessToken은 Redis에 저장한다.

이때 AccessToken과 함께 회원 pk 그리고 권한을 함께 저장한다.

RefreshToken과 AccessToekn은 프런트로 전달한다.

---

## 인가

### 인가 과정

프런트로 전달한 AccessToken과 사용자의 pk값을 전달받아 유효성을 확인한다.

유효성이 확인되면 Redis에서 AccessToken과 동일한 정보를 조회한다.

조회한 정보와 프런트에서 전달받은 pk값이 동일한지 확인한다.

이후 api에 따른 권한을 확인하여 인가를 승인해준다.

### 관리자 권한 요청

관리자 권한 요청한 회원이 관리자가 아닌 회원인지 확인한다.

관리자 권한을 요청한 회원의 아이디 이메일으로 관리자 확인 키를 보낸다.

보낸 키와 입력받은 키가 동일한지 확인한 후 일치하면 권한 변경을 승인한다.