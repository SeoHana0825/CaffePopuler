# CH6 실전 - 서버 개발 과제
#### Spring boot 기반의 대용량 트래픽 서버 개발 프로젝트입니다.

#### 대용량 트래픽의 상황에서도 안정적으로 동작하는 커피숍 주문 시스템을 구현을 목적으로 구현하였습니다. 

---
## 설계 구성
### 1. ERD
#### | 데이터베이스 엔티티 관계 다이어그램

### 2. 주요 API
#### | 주요 API 명세서
- 회원 (`"/api`)

| Method | URL         | 설명           |
|----|-------------|--------------|
|POST| `"/signup"` | 회원가입 (session) |
|POST| `"/login"`  | 로그인          |
|POST| `"/logout"` | 로그아웃         |

- 주문 (`"/api/orders"`)

| Method | URL | 설명    |
|----|-----|-------|
|POST| -   | 주문 생성 |

- 결제 (`"/api/payments"`)

| Method | URL             | 설명            |
|--------|-----------------|---------------|
| POST   | `"/{orderId}"`   | 결제 진행 (포인트 결제) |
| GET    | `"/{paymentId}"` | 결제 명세서 조회     |

- 포인트 (`"/api/admin/points"`)

| Method | URL                   | 설명          |
|----|-----------------------|-------------|
|POST| `"/charge/{userId}?amount=500000"` | 관리자가 포인트 부여 |

- 메뉴 (`"/api/menus"`)

| Method | URL           | 설명                   |
|--------|---------------|----------------------|
| POST   | -             | 메뉴 생성                |
| GET    | -             | 메뉴 목록 조회             |
|POST| `"/{menuId}"` | 메뉴 단건 조회             |
| GET    | `"/popular"`  | 메뉴 인기 조회 (7일간 top 3) |

### 3. 로드맵을 구상
#### | 주요 기능 설계 고민 정리
정리 : [오버엔지니어링의 유혹을 미뤄두고 "데이터 정합성"이라는 기본기에 집중](https://velog.io/@onnuri1226/%EA%B0%9C%EC%9D%B8-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B8%B0%ED%9A%8D-%EC%98%A4%EB%B2%84%EC%97%94%EC%A7%80%EB%8B%88%EC%96%B4%EB%A7%81%EC%9D%98-%EC%9C%A0%ED%98%B9%EC%9D%84-%EB%AF%B8%EB%A4%84%EB%91%90%EA%B3%A0-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%95%ED%95%A9%EC%84%B1%EC%9D%B4%EB%9D%BC%EB%8A%94-%EA%B8%B0%EB%B3%B8%EA%B8%B0%EC%97%90-%EC%A7%91%EC%A4%91)

### 4. 기술 스택

|분류| 기술                                                                 |
|--|--------------------------------------------------------------------|
|Language| java 17                                                            |
|Framework| Spring Boot, Spring Security, Spring Data JPA, Lombok, MySQL Driver |
|Database| MySQL                                                              |
|Cache| Redis (StringRedisTemplate)                                        |
|Auth| session                                        |
|Concurrency| 비관적 락                                   |
|Build|Gradle|

---
## 핵심 기술 및 설계 의도
### 1. 따닥 이슈 - 중복 결제 방어 (동시성 해결)
- **문제 상황** : 
  - 사용자가 결제 버튼을 연속으로 두 번 누르거나 네트워크 지연으로 인해 재요청이 발생하면 동일한 결제가 두 번 중복 처리되는 문제 발생 가능성을 생각해보았습니다
  - 선착순 할인과 같은 대용량 트래픽이 있을 이벤트에서 많은 사용자가 동시에 결제 시도를 하게되면 동일 재화의 갱신 유실과 사용자의 재화(돈/포인트)유실 발생 가능성을 생각해보았습니다.
- **분석 내용** :
  - 시퀀스 vs. 멱등키(UUID)
    - 시퀀스 (Sequence) : 데이터 크기가 작고 인덱스로 정렬이 되어 검색이 빠르다는 장점이 있지만 커스텀 번호 생성 및 번호가 **순서**대로 생성되기 때문에 데이터 번호 유추가 쉽고 DB에 데이터를 완전히 저장하기 전까지는 번호를 알 수 없는 단점 있음
    - 멱등키 (UUID) : 시퀀스와 비교해 무작위 랜덤값으로 DB 인덱스 성능이 떨어지고, DB 비용이 크지만 DB에 접근하기 전에 고유값이 랜덤으로 생성되 외부 유추가 어렵고 데이터 분산 시, 충돌 위험이 적은 장점 있음
  - 낙관적 락 vs. 비관적 락 vs. Redis 분산 락
    - 낙관적 락 : <p>결제 시도 시도에서 락을 잡지 않아 DB 성능이 좋지만, 결제 마지막 단계에서 충돌이 발생하면 예외 처리 및 재시도를 해야하기 때문에 사용자 경험의 신뢰성 하락과 성능 저하의 위험 있음
    - 비관적 락 : <p>결제 마지막에 이뤄지는 충돌 자체를 처음부터 원천 차단하기 때문에 데이터 정합성을 보장하고 안전하지만 사용자가 락을 길게 쥐고 있으면 병목 현상 또는 데드락 발생으로 성능이 느려질 수 있음
    - Redis 분산 락 : <p>DB에 부하를 주지 않고 서버 환경에서 안전하게 락을 제어할 수 있고 여러 서버에서 들어오려는 시도를 해도 **비즈니스 로직 전체**에 락이 걸려있기 때문에 안전한 반면 외부 DB를 사용하기 때문에 외부 서버가 다운되면 락이 해지될 가능성이 있음
- **기술적 선택 이유**

  - **UUID 발급 + DB Unique 제약조건** <p> UUID 의 단점인 DB 인덱스 성능 저하와 비용이 큰 점을 생각해 DB unique 제약조건으로 PK 에 인덱싱을 해주는 방식으로 중복 요청이 들어와도 UUID에서 한번 거르고 `orderNo`의 unique 제약 조건으로 인해 예외가 발생해 자연스럽게 중복 결제를 차단할 수 있게했습니다.
```java
@Column(nullable = false, unique = true, length = 225)
private String orderNo;

public static Order register (
             String orderNo,
             User user
    ) {
        Order order = new Order();

        order.totalAmount = 0L;
        order.orderNo = orderNo != null ? orderNo : UUID.randomUUID().toString();
        order.status = OrderStatus.CART;
        order.user = user;

        return order;
}
```
  - **비관적 락** <p> 이번에는 낙관적 락을 사용해보려고 했지만, 역시 결제/포인트는 성능보다 **데이터 정합성**을 먼저라고 판단해 이번에도 비관적 락을 선택해봤으며 마지막에 충돌을 발견해 사용자들에게 예외 또는 재시도를 요청하는 것보다 시도가 조금 느려도 안전하고 확실하게 처리를 해주는게 좋을 것 같다고 생각했습니다.
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Point p WHERE p.user.id = :userId")
Optional<Point> findByUserIdWithPessimisticLock (@Param("userId") Long userId);
```
- 기술블로그 정리 : 
  - [중복 결제 따닥 이슈를 방지하기 위한 가장 효율적인 방법은? : Sequence vs UUID](https://velog.io/@onnuri1226/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%A4%91%EB%B3%B5-%EA%B2%B0%EC%A0%9C-%EB%94%B0%EB%8B%A5-%EC%9D%B4%EC%8A%88%EB%A5%BC-%EB%B0%A9%EC%A7%80%ED%95%98%EA%B8%B0-%EC%9C%84%ED%95%9C-%EA%B0%80%EC%9E%A5-%ED%9A%A8%EC%9C%A8%EC%A0%81%EC%9D%B8-%EB%B0%A9%EB%B2%95%EC%9D%80-Sequence-vs-UUID)
  - [동시성 처리 고민](https://velog.io/@onnuri1226/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC)

### 2. 외부 API 연동 시 장애 분리 (비동기 + 이벤트)
- **문제 상황** : 결제 완료 후 외부 플랫폼으로 데이터를 전송해야하는 하는데, 외부 API가 지연되거나 에러를 반환하면 어떻게 할까?를 생각해보았습니다.
- **분석 내용** :
  - `@Transactional` 내부 동기 호출 <p> 로직이 한곳에 모여있어 코드 흐름을 파악하기 쉽지만, 외부 API 지연 시 DB 트랜잭션 시간이 길어져 서버 전체가 멈춰버릴 수 있는 가능성이 있음
  - `@Async` 비동기 호출 <p> 별도로 스레드가 동작해 속도가 빠르고, 사용자 응답 시간에 지연이 없지만, 비동기로 외부 통신은 성공했지만 직후 메인 트랜잭션이 DB 저장 단계에서 실패하게되면 외부 플랫폼에 **가짜 데이터**가 쌓일 수 있음
  - `@TransactionalEventListener` 스프링 이벤트 <p> 카프카 등 추가적인 인프라구축 없이 애플리케이션 내부에서 의존성 분리가 가능하고, 커밋 직후에만 동작과 동시에 비동기 처리로 응답 속도가 빠르나 서버가 갑자기 다운되거나 재시작될 경우, 이벤트가 유실 될 가능성이 있음
  - kafka 도입 <p> 결제 서버와 통계 서버를 완벽히 분리할 수 있고, 대용량 트래픽이 있어도 정보는 카프카 안에 안전히 보관되 메시지 유실이 낮지만 별도의 구축환경과 인프라, 운영, 유지보수 등의 비용이 증가함과 동시에 구현까지 시간이 엄청 걸릴 것 같음
- **기술적 선택 이유**
  - `@TransactionalEventListener` + `@Async` 처리 <p> `@TransactionalEventListener(AFTER_COMMIT)` 을 적용해 결제 트랜잭션이 성공적으로 DB에 반영된 **후**에만 이벤트가 발행하도록해 사용자의 혼선을 줄였고, `@Async`를 적용해 외부 API 통신을 **비동기 스레드**로 분리해 고객의 결제 응답 속도에는 영향을 주지 않게했으며, `ThreadPoolTaskExecutor`로 스레드 생성을 조절해 서버가 터져버리는 것을 방지해보았습니다.
```java
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "dataPlatformTaskExecutor")
    public Executor dataPlatformTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본 유지 스레드 수
        executor.setMaxPoolSize(10); // 최대 생성 가능한 스레드 재한 수
        executor.setQueueCapacity(50); // 스레드가 꽉 찼을 때 대기하는 큐 크기
        executor.setThreadNamePrefix("Mock-Async");
        executor.initialize();
        return executor;
    }
}
```
  - `ApplicationEventPublisher` 의존성 분리 <p> 결제 도메인과 데이터 전송 도메인의 의존성을 분리해서 사용자에게 응답 솔도가 빠르게 했고, 서버에 에러가나도 비즈니스로직과 알림 전송 로직이 독립적으로 유지되서 서로 영향이 없습니다.
```java
@Getter
@Builder (access = AccessLevel.PRIVATE)
public class OrderCompletedEvent {

    private final String orderNo;
    private final Long userId;
    private final Long paymentAmount;
    private final List<MenuRankInfo> MenuRankInfos;

    public static OrderCompletedEvent from (Payment payment) {
        return OrderCompletedEvent.builder()
                .orderNo(payment.getOrder().getOrderNo())
                .userId(payment.getUser().getId())
                .paymentAmount(payment.getAmount())
                .MenuRankInfos(
                        payment.getOrder().getOrderItemList().stream()
                                .map(item -> new MenuRankInfo(item.getNameSnap(),item.getQuantity()))
                                .collect(Collectors.toList())
                ).build();
    }
}
```
- 기술블로그 정리 :

### 3. 실시간 인기 메뉴 조회
- 문제 상황
- 분석 내용
- 기술적 선택 이유
- 기술블로그 정리 :

## 다음 스탭
단일 서버 환경에서의 아키텍쳐로 구현을 해보았지만, 추후 인프라 확장 및 iOS 운영 등 생각을 확장시켜봤을 때
- [인증/인가] Session ➔ JWT 기반 Stateless 아키텍처 <p> session 으로 구성한 인증/인가를 JWT 기반으로 stateless하게 변경
- [동시성 제어] 비관적 락 ➔ Redis 분산 락 <p> 비관적 락으로도 충분 할 것 같지만, DB 부하 최소화를 위한 Redis 분산 락 환경으로 변경
- [비동기 처리] Spring Event + @Async ➔ Message Queue (Kafka) <p> 서버 장애 시 메모리 이벤트 유실 방지를 위해 메시지 큐 (kafka 등) 도입

으로 추가적으로 로드맵을 구상해보았습니다.

---
## 프로젝트 구조
    caffePopularProject
    ├── main
    │    ├── common
    │    │    ├── config        # 외부 API 연동 시 장애 분리 및 회원 가입 인증/인가, RedisConfig
    │    │    ├── dto           # 공통 처리 정보
    │    │    ├── entity        # DateTime
    │    │    ├── exception     # Error 관리
    │    │    ├── interceptor   # session 인가
    │    └── domain
    │         ├── menu          # 메뉴 생성 및 조회
    │         ├── order         # 주문 생성 및 따닥 이슈 1차 방지
    │         ├── payment       # 포인트 결제, 비관적 락, 및 비동기 이벤트 설정
    │         ├── point         # 포인트 지갑, 관리자 포인트 충전, 비관적 락
    │         └── user          # 회원 가입, 로그인 session 적용
    └── test
