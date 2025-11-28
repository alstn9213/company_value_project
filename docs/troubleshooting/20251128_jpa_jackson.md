# 트러블슈팅: JPA 인덱스 네이밍 및 Jackson 날짜 직렬화 오류

**작성일:** 2025-11-28  
**관련 서비스:** Backend (Spring Data JPA, Jackson), Database (MariaDB)

---

## 1. Issue: JPA Index Naming 오류

### 문제 상황 (Problem)
* 애플리케이션 구동 시 `InvalidDataAccessResourceUsageException` 예외 발생.
* "Key column 'recordedDate' doesn't exist in table" 메시지와 함께 DB 테이블 생성 또는 업데이트 실패.

### 원인 (Cause)
* **JPA와 DB의 네이밍 전략 불일치:**
    * Java Entity에서는 카멜 케이스(`recordedDate`)를 사용.
    * Spring Boot의 기본 네이밍 전략(`SpringPhysicalNamingStrategy`)은 이를 스네이크 케이스(`recorded_date`)로 변환하여 DB에 테이블을 생성함.
    * 하지만 `@Table(indexes = @Index(columnList = "recordedDate"))` 어노테이션에서 인덱스 컬럼명을 지정할 때는 **실제 DB에 생성된 컬럼명**을 기준으로 해야 하는데, Java 필드명을 그대로 사용하여 매핑에 실패함.

### 해결 (Solution)
* `@Index` 어노테이션의 `columnList` 값을 실제 DB 컬럼명인 스네이크 케이스로 수정함.

```java
// 변경 전 (에러 발생)
@Table(name = "stock_price_history", indexes = @Index(columnList = "company_id, recordedDate"))

// 변경 후 (정상 작동)
@Table(name = "stock_price_history", indexes = @Index(columnList = "company_id, recorded_date"))
```

----

## 2. Issue: JSON 날짜 직렬화 문제 (Date Serialization)

### 문제 상황 (Problem)

  * 프론트엔드 차트 라이브러리(`Recharts`)와 날짜 처리 라이브러리(`dayjs`)에서 백엔드로부터 받은 날짜 데이터를 인식하지 못함.
  * API 응답 확인 결과, 날짜 필드가 문자열이 아닌 배열 형태(`[2025, 11, 28]`)로 내려오고 있음.

### 원인 (Cause)

  * **Jackson 라이브러리의 기본 동작:**
      * Spring Boot에 내장된 Jackson 라이브러리는 `LocalDate` 타입을 직렬화할 때, 기본적으로 타임스탬프(배열) 형식을 사용함 (`WRITE_DATES_AS_TIMESTAMPS = true`).
      * 프론트엔드에서는 ISO 8601 표준 문자열(`"YYYY-MM-DD"`) 포맷을 기대하고 있어 파싱 에러가 발생함.

### 해결 (Solution)

  * `application.properties`에 설정을 추가하여 날짜를 배열이 아닌 ISO 포맷 문자열로 직렬화하도록 변경함.


```properties
# application.properties
spring.jackson.serialization.write-dates-as-timestamps=false
```

  * **결과:** API 응답이 `[2025, 11, 28]`에서 `"2025-11-28"`로 변경되어 프론트엔드에서 정상적으로 차트가 렌더링됨.
