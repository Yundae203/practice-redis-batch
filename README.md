# 기업 과제 핵심 기능 구현 내용

## 핵심 기능

### 리뷰 생성 비동기 처리

여러명의 유저가 동시에 리뷰를 생성할 때 응답속도를 높이기 위해 비동기 처리를 사용했습니다.

생성할 때마다 상품의 점수와 리뷰 갯수를 업데이트 할 시 데이터베이스가 경쟁상태에 빠질 것을 우려했습니다.

따라서, 상품을 바로 업데이트하는 대신 리뷰로부터 점수를 캐싱하여 저장하여 10분마다 배치 처리를 통해 일괄 업데이트를 진행했습니다.

```java
    @Async
    @Transactional
    public void saveProductReview(ReviewRequest reviewRequest, Long productId, String imageUrl) {
        boolean exists = reviewService.existsByProductIdAndUserId(productId, reviewRequest.userId());
        // 비즈니스 요구사항: 하나의 상품에 하나의 리뷰만을 생성할 수 있다.
        validate(!exists, "이미 리뷰가 존재하는 상품입니다.");

        Review review = reviewRequest.toModel(imageUrl, productId, customClock.now()); // 리뷰 생성
        redisService.setValue(review.getProductId(), ReviewCache.from(review)); // 리뷰 점수 캐싱

        reviewService.save(review); // 리뷰 저장
    }
```
### 동시성 문제 해결을 위한 레디스 트랜잭션

레디스에서 캐시된 정보를 가져온 즉시 삭제하여 업데이트를 진행합니다.

가져와서 삭제하는 사이에 새로운 요청이 들어와 데이터의 정합성이 깨지는 것을 방지하기 위해

`@Transactional`을 활용하여 비관적 락을 통해 정합성을 보장했습니다.

```java
@Component
@RequiredArgsConstructor
public class ReviewCacheRedis implements RedisService<ReviewCache, Long> {

    // 그 외 레디스 접근을 위한 메서드

    @Override
    @Transactional
    public ReviewCache getAndRemoveValue(Long key) {
        ReviewCache cache = getValue(key);
        deleteValue(key);
        return cache;
    }

}

@Configuration
@EnableTransactionManagement
public class RedisConfig {

    // 그 외 레디스 관련 설정

    @Bean
    public PlatformTransactionManager transactionManager() {  // (3)
        return new JpaTransactionManager();
    }
}
```

### 상품 응답 시 정합성을 위한 캐시 확인

배치를 통한 일괄 업데이트는 10분에 한 번 발생하기 때문에

상품은 응답할 시 캐시를 확인하여 정합성을 보장합니다.

```java
    public ProductReviews getProductReviews(Long productId, Long cursor, Integer size) {
        Product product = productService.findById(productId);
        Slice<Review> reviewSlice = reviewService.findAllByProductId(productId, cursor, size);

        ReviewCache cache = redisService.getValue(productId); // 캐시해 둔 리뷰 점수 총합
        if (cache != null) {
            // 아직 업데이트 되지 않은 캐시를 반영하여 응답에 포함하여 정합성 보장
            product.updateCountAndScore(cache.count(), cache.calculatedScore()); 
        }

        // 리뷰가 하나 이상 존재할 경우 커서 반환
        Long nextCursor = null;
        if (reviewSlice.getNumberOfElements() > 0) {
            nextCursor = reviewSlice.getContent().get(reviewSlice.getNumberOfElements() - 1).getId();
        }

        return ProductReviews.of(product, nextCursor, reviewSlice.getContent());
    }
```

# 요구 사항 분석 내용

## **비즈니스 요구 사항 분석**

### 1. 리뷰는 존재하는 상품에만 작성할 수 있습니다.

<aside>
📢

**분석**

데이터의 무결성이 중요

- 리뷰 생성 전에 상품의 존재 여부를 확인
- 상품을 물리적 논리적 삭제 할 때, 리뷰에 대한 추가 조작 필요

**의사 결정**

요구 사항에 삭제와 관련된 부분은 없기 때문에
리뷰 생성 시 데이터 무결성을 신경 써서 체크하면 된다.

</aside>

---

### 2. 유저는 하나의 상품에 대해 하나의 리뷰만 작성 가능합니다.

<aside>
📢

**분석**

상품과 리뷰의 연관관계와 PK 설정

- 상품과 리뷰는 1대 다 관계이다
- 유저와 리뷰는 1대 1 관계이다
- 상품과 유저 ID를 복합키로 PK 설정을 고려할 수 있다

**의사 결정**

복합키를 사용하면 무결성이 상승할 것이다.

그러나 API 스펙을 확인했을 때, 특정 상품에 대한 모든 리뷰를 조회한다.

조회에 있어 유저 ID가 주요 역할을 수행하지 못하기 때문에 복합키를 사용하는 것보다

리뷰의 자체 ID를 생성하여 상품 ID에 인덱스를 설정하면 조회 성능에 유리할 것으로 판단

복합키는 고려하지 않고 리뷰의 자체 ID 생성으로 결정

</aside>

---

### 3. 유저는 1~5점 사이의 점수와 리뷰를 남길 수 있습니다.

<aside>
📢

**분석**

API의 입력값 검증

유저가 1~5점이 아닌 범위의 입력값을 주었을 때 어떻게 처리할 것인가?

1. 잘못된 입력값이라는 응답을 반환하여 재요청을 요구한다.
2. 0점을 주면 1점으로 5점 이상을 주면 5점으로 처리한다.

**의사 결정**

2번 방식을 채택하면 편의성을 제공할 수 있으나 API가 1점에서 5점 사이의 점수를 요구한다는 명확성을 보여줄 수가 없다.

따라서, 1번 방식으로 구현할 예정

</aside>

---

### 4. 사진은 선택적으로 업로드 가능합니다.

- 사진은 S3 에 저장된다고 가정하고, S3 적재 부분은 dummy 구현체를 생성합니다.
(실제 S3 연동을 할 필요는 없습니다.)

<aside>
📢

**분석**

Dummy 객체를 구현할 수 있는가?
이미지는 Nullable한 값이다.

- Dummy는 요청을 소비만 하고 어떠한 동작도 하지 않는 객체를 뜻한다.
- 특정 행동을 수행하면 Fake객체다.

**의사 결정**

S3 인터페이스 학습 후 Dummy 객체 생성

</aside>

---

### 5. 리뷰는 '가장 최근에 작성된 리뷰' 순서대로 조회합니다.

<aside>
📢

**분석**

데이터 조회 시 정렬 기준 선정

**의사 결정**

논리적으로 생성 시점이 작성 시간과 같음으로 ID 기준으로 정렬

</aside>

---

## **기술적 요구 사항 분석 **

### 1. Mysql 조회 시 인덱스를 잘 탈 수 있게 설계해야 합니다.

<aside>
📢

**분석**

데이터 조회를 위해 인덱스를 올바르게 설정할 수 있는가?

**의사 결정**

비즈니스 요구 사항에선 특정 상품에 대한 모든 리뷰를 조회

따라서, 리뷰의 ID와 상품 ID를 기준으로 인덱스를 설정하여 조회 성능 상승

</aside>

---

### 2. 상품 테이블에 reviewCount 와 score 가 잘 반영되어야 한다.

<aside>
📢

**분석**

상품 테이블에 reviewCount와 score를 관리하기 위해 어떤 전략을 취할 것인가?

간단하게 구현하면 리뷰를 작성할 때마다 상품 테이블을 업데이트 할 수 있음

위의 방식을 채택하면 트래픽이 상승했을 때, DB에 자원이 부족할 수 있음

대안으로 아래와 같은 방식이 있음

1. 캐시와 스케쥴러를 사용하여 리뷰를 모았다가 배치 처리 (Write Back)
2. MySQL의 Trigger 활용
3. 상품을 조회할 때 전체 리뷰를 가져와 계산하여 적용

**의사 결정**

1번 방식은 현재 실력으로는 구현 난이도가 높음

2번 방식은 DB 자원이 소모되는 것은 결과적으로 같아서 문제의 해결 방법이 아님

3번 방식은 리뷰의 수가 늘어나면 늘어날 수록 계산할 값이 늘어나 조회가 느려지게 됨

따라서, 구현 난이도가 있더라도 기능적 측면을 고려하여 1번 방식 채택

</aside>

---

### 3. (Optional) 동시성을 고려한 설계를 해주세요. 많은 유저들이 동시에 리뷰를 작성할 때, 발생할 수 있는 문제를 고려해보세요.

<aside>
📢

**분석**

데이터 베이스에 다수의 요청이 접근하면서 데이터베이스가 경쟁적인 상태에 빠지게 된다.

캐시와 스케쥴러를 사용하여 상품 테이블에 대한 업데이트는 경쟁에서 제외했기 때문에 어느 정도 완화될 것이라고 생각한다.

유저들의 요청 또한, 비동기 처리를 하면 응답 속도를 개선할 수 있을 거라고 본다.

**의사 결정**

리뷰 생성 요청을 비동기 처리하여 응답속도를 상승하고 레디스에 점수를 저장한 후 배치 처리를 통해 일괄 업데이트

</aside>

