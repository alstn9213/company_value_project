# 트러블슈팅: 통합 배포 시 화이트라벨 에러(401) 해결

**작성일:** 2025-12-03  
**관련 모듈:** Spring Security, React, Deployment

-----

## 1. 문제 상황 (Problem)

프론트엔드(React)와 백엔드(Spring Boot)를 **통합 배포(Monolithic Deployment)** 방식으로 전환한 후, 배포된 서버(`http://34.69.43.90:8080`)에 접속했을 때 화면이 뜨지 않고 **Whitelabel Error Page**가 발생함.

  * **증상:** 브라우저에 리액트 화면 대신 스프링 부트 기본 에러 페이지 출력.
  * **에러 메시지:**
    ```text
    Whitelabel Error Page
    This application has no explicit mapping for /error, so you are seeing this as a fallback.
    Status: 401 Unauthorized
    Message: There was an unexpected error (type=Unauthorized, status=401).
    ```
  * **개발자 도구(Network) 확인 결과:** `index.html`, `main.js`, `index.css` 등 정적 파일 요청이 모두 `401 Unauthorized`로 차단됨.

-----

## 2. 원인 분석 (Root Cause)

### A. 배포 구조 변화에 따른 보안 컨텍스트 변화

  * **기존(로컬 개발):** 프론트엔드(`localhost:5173`)와 백엔드(`localhost:8080`)가 분리되어 있었음. 브라우저가 프론트엔드 서버에 요청할 때는 스프링 시큐리티를 거치지 않음.
  * **변경(통합 배포):** 리액트 빌드 결과물(`dist/*`)을 스프링 부트의 `src/main/resources/static`으로 이동시켜 **백엔드 서버가 정적 파일까지 서빙**하도록 변경함.

### B. Spring Security의 기본 정책

  * Spring Security는 기본적으로 **"모든 요청에 대해 인증(로그인)을 요구"**함 (`anyRequest().authenticated()`).
  * 이로 인해 사용자가 웹사이트에 처음 접속할 때 필요한 **HTML, JS, CSS 파일조차 로그인하지 않은 사용자라는 이유로 차단**되어 401 에러가 발생한 것임.

-----

## 3. 해결 과정 (Solution)

### Step 1: SecurityConfig 설정 수정

정적 리소스 경로에 대해서는 **인증 없이 접근 가능하도록(`permitAll`)** 시큐리티 필터 체인을 수정함.

  * **수정 파일:** `SecurityConfig.java`
  * **수정 내용:** `authorizeHttpRequests` 설정에 정적 파일 패턴 추가.

<!-- end list -->

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ... (CSRF 등 기존 설정)
        .authorizeHttpRequests(auth -> auth
            // 1. React 정적 리소스 허용 (대문, 정적파일, 파비콘 등)
            .requestMatchers("/", "/index.html", "/assets/**", "/*.ico", "/vite.svg").permitAll()
            
            // 2. 인증이 필요 없는 API 허용 (로그인, 회원가입 등)
            .requestMatchers("/auth/**", "/api/macro/**").permitAll()
            
            // 3. 그 외 요청은 인증 필요
            .anyRequest().authenticated()
        );
        // ...
    return http.build();
}
```

### Step 2: SPA 라우팅 문제 해결 (추가 조치)

리액트 라우터(`react-router-dom`)를 사용하는 SPA(Single Page Application) 특성상, **새로고침 시 404 에러**가 발생할 수 있음을 인지하고 **Forwarding Controller**를 추가함.

  - **문제:** `/companies` 같은 경로는 실제 파일이 아니라 리액트 내부 라우팅 경로임. 스프링은 이를 API 요청으로 착각하고 404를 반환함.
  - **해결:** API 요청(`api/**`)이 아닌 모든 경로는 `index.html`로 포워딩하여 리액트가 라우팅을 처리하도록 위임.


```java
// WebController.java
@Controller
public class WebController {
    // 점(.)이 없는 경로(확장자가 없는 경로)는 모두 index.html로 포워딩
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
```

-----

## 4. 결과 및 배운 점 (Conclusion)

  - **결과:** 설정 적용 후 재배포 시, 메인 화면 진입 및 새로고침이 정상적으로 작동함. API 호출과 정적 파일 제공이 하나의 서버에서 보안 충돌 없이 수행됨.
  - **배운 점:**
    1.  **통합 배포의 특성:** 프론트엔드 리소스도 백엔드의 '관리 대상'이 되므로, 보안 설정 시 이를 반드시 고려해야 한다.
    2.  **Spring Security의 유연성:** URL 패턴 매칭(`requestMatchers`)을 통해 정교한 접근 제어(Authorization)가 가능하다는 것을 실습을 통해 확인함.