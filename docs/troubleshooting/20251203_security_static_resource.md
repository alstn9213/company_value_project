# 트러블슈팅: 통합 배포 시 화이트라벨 에러(401) 해결

**작성일:** 2025-12-03  
**관련 모듈:** Spring Security, React, Deployment

-----
## 1. 배경 및 문제 상황 (Problem)

개인 프로젝트 진행 도중 비용 절감과 배포 편의성을 위해 배포 구조를 **AWS 분리 배포**에서 **GCP 통합 배포(Monolithic Deployment)** 방식으로 전환했다. (React 빌드 파일(`dist`)을 Spring Boot의 `static` 폴더에 넣어 단일 `.jar`로 배포하는 방식)

하지만 배포 후 서버(`http://34.xx.xx.xx:8080`)에 접속했을 때, 기대했던 메인 화면 대신 **Whitelabel Error Page**가 발생했다.

* **에러 메시지:**
```text
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
Status: 401 Unauthorized
Message: There was an unexpected error (type=Unauthorized, status=401).

```


* **네트워크 탭(Network) 확인:**
개발자 도구(f12)를 누르고 네트워크 탭을 확인하니`index.html`, `main.js`, `index.css` 등 화면을 그리는 데 필요한 **모든 정적 파일 요청이 401 에러로 차단**되고 있었다.

---

## 2. 원인 분석 (Root Cause)

### A. 배포 구조 변화에 따른 요청 흐름 변경

* **로컬 개발 환경:** 로컬에서 개발할 때는 React(`localhost:5173`)와 Spring Boot(`localhost:8080`)가 분리되어 실행했다. 브라우저가 화면을 요청할 때는 React Dev Server로 요청하므로 **Spring Security를 거치지 않아** 문제가 없었다.
* **통합 배포 환경:** 하지만 React 빌드 결과물이 Spring Boot 내부(`resources/static`)로 들어갔으니, 이제 브라우저가 **HTML/JS/CSS 파일을 요청할 때도 Spring Boot 서버가 응답**해야 하며, 이 과정에서 **Spring Security 필터 체인**을 통과한다.

### B. Spring Security의 기본 정책 충돌

Spring Security는 기본 설정상 `anyRequest().authenticated()` 정책을 가집니다. 즉, **"모든 요청에 대해 인증(로그인)을 요구"**한다.
이 때문에 로그인하지 않은 사용자가 웹사이트에 처음 접속할 때 필수적인 **정적 리소스(HTML, JS, CSS)조차 "인증되지 않은 요청"으로 간주되어 차단**된 것이다.

---

## 3. 해결 과정 (Solution)

문제를 해결하기 위해 **1) 정적 리소스 접근 허용**과 **2) SPA 라우팅 처리** 두 가지 작업을 진행했다.

#### Step 1: SecurityConfig 설정 수정 (정적 리소스 허용)

Spring Security 설정에서 정적 파일 경로에 대해 **인증 없이 접근 가능하도록(`permitAll`)** 예외 처리를 추가했다.

* **수정 파일:** `SecurityConfig.java`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ... (CSRF, CORS 등 기존 설정)
        .authorizeHttpRequests(auth -> auth
            // 1. React 정적 리소스 및 루트 경로 허용
            // index.html, assets 폴더 내의 파일, 파비콘 등은 로그인 없이 접근 가능해야 함
            .requestMatchers("/", "/index.html", "/assets/**", "/*.ico", "/vite.svg").permitAll()
            
            // 2. 인증이 필요 없는 공개 API 허용 (로그인, 회원가입 등)
            .requestMatchers("/auth/**", "/api/public/**").permitAll()
            
            // 3. 그 외의 모든 요청은 인증 필요
            .anyRequest().authenticated()
        );
        
    return http.build();
}

```

#### Step 2: SPA 라우팅 문제 해결 (Forwarding Controller 추가)

React Router(`react-router-dom`)를 사용하는 SPA 특성상, 사용자가 브라우저 주소창에 `/companies`와 같은 경로를 직접 입력하거나 새로고침을 하면 문제가 발생한다. Spring Boot는 해당 경로에 매핑된 컨트롤러가 없으므로 **404 Not Found**를 반환하기 때문이다.

이를 해결하기 위해 **API 요청을 제외한 모든 경로를 `index.html`로 포워딩**하여, 라우팅 처리를 React에게 위임하는 컨트롤러를 추가했다.

* **추가 파일:** `WebController.java` (또는 `ForwardingController.java`)

```java
@Controller
public class WebController {

    /**
     * Client-side Routing 처리
     * 리소스 파일(확장자가 있는 파일)이 아닌 모든 경로 요청을 index.html로 포워딩합니다.
     * 이를 통해 React Router가 경로를 인식하고 올바른 화면을 렌더링할 수 있습니다.
     */
    @GetMapping(value =  "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}

```

---

## 4. 결과

위 설정을 적용한 후 다시 빌드 및 배포를 진행한 결과, 메인 페이지가 정상적으로 로딩되었으며 새로고침 시에도 404 에러 없이 React 화면이 잘 유지되는 것을 확인했다.

