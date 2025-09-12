# 매칭엔진 통합 PRD (Product Requirements Document)

## 1. 개요

### 1.1 목적
homecare_matching 레거시 서비스를 homecare_BE Spring Boot 애플리케이션에 통합하여 버스 지수를 향상시키고 운영 복잡성을 감소시킵니다.

### 1.2 배경
- 현재 매칭 기능이 별도의 Python FastAPI 서비스로 분리되어 있음
- REST API를 통한 통신으로 인한 네트워크 오버헤드 발생
- 두 개의 서비스를 독립적으로 운영해야 하는 복잡성

### 1.3 목표
- 버스 지수 향상: 단일 서비스로 통합
- 성능 개선: 네트워크 통신 오버헤드 제거
- 유지보수성 향상: 통합된 코드베이스 관리

## 2. 기능 요구사항

### 2.1 핵심 기능

#### 2.1.1 근무조건 정규화 (Work Condition Normalization)
**기능 설명**: 자연어로 입력된 근무조건을 시스템이 처리할 수 있는 논리조건으로 변환

**요구사항**:
- 자연어 입력 지원 (한국어)
- LLM 기반 정규화 정확도 95% 이상
- 처리 시간 1초 이내

**입력 예시**:
```
"오전 9시부터 오후 6시까지, 주 3회, 
치매 환자 경험 있으신 분, 차량 소지자"
```

**출력 예시**:
```json
{
  "time_range": {
    "start": "09:00",
    "end": "18:00"
  },
  "frequency": "3회/주",
  "experience": ["치매"],
  "vehicle": true
}
```

#### 2.1.2 스케쥴링 (Scheduling)
**기능 설명**: 신청건의 위치를 기준으로 적합한 요양보호사를 찾고 최적의 일정을 제안

**요구사항**:
- N km 반경 내 요양보호사 검색
  - 해당 반경은 코드 내에서 어디서 변경하면 바꿀 수 있는지 쉽게 추적할 수 있어야 함(예: 환경변수 사용)
  - <!-- TODO 환경변수가 없을 때는, 프로그램을 실행시키지 않도록 오류 반환 -->
- ETA 계산 및 Top-5 후보군 선정
- 처리 시간 2초 이내

**알고리즘**:
1. 1차 필터링: N km 반경 내 요양보호사 선별
2. ETA 생성: 각 후보별 예상 도착 시간 계산
3. 순위 결정: 거리 및 가용성 기반 순위 결정

#### 2.1.3 매칭 (Matching)
**기능 설명**: 정규화된 근무조건과 스케쥴링 결과를 종합하여 최적의 매칭 결과 도출

**요구사항**:
- 근무조건 만족도 90% 이상
- 이동시간 최소화
- 최대 5개 후보 제공
- 처리 시간 1초 이내

**매칭 로직**:
1. 근무조건 필터링: 정규화된 조건과 요양보호사 프로필 비교
2. 가중치 계산: 조건 만족도, 거리, 가용성 등을 종합한 점수 계산
3. 순위 결정: 가중치 기반 최종 순위 결정

### 2.2 부가 기능

#### 2.2.1 실시간 모니터링
- 매칭 진행 상황 실시간 추적
- 시스템 성능 메트릭 수집
- 에러 로그 및 알림

#### 2.2.2 매칭 히스토리
- 과거 매칭 결과 조회
- 매칭 성공률 통계
- 개선점 분석 데이터

#### 2.2.3 설정 관리
- 매칭 알고리즘 파라미터 조정
- 가중치 설정
- 시스템 설정 변경

## 3. 기술 요구사항

### 3.1 개발 환경
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.3
- **데이터베이스**: PostgreSQL 14+
- **통신**: REST API (내부)
- **LLM**: OpenRouter API (Google Gemini 2.5 Flash)

### 3.2 성능 목표
- 매칭 응답 시간: 3초 이내
- 매칭 정확도: 90% 이상
- 시스템 가용성: 99.9% 이상
- 인프라 유지보수 컴포넌트 최소화

### 3.3 위치 정보 형식
시스템에서 사용하는 위치 정보는 다음과 같은 표준 형식을 따릅니다:

```json
{
    "roadAddress": "서울특별시 강남구 테헤란로 123",
    "jibunAddress": "서울특별시 강남구 역삼동 123-45",
    "addressElements": [
        {"type": "sido", "name": "서울특별시"},
        {"type": "sigungu", "name": "강남구"},
        {"type": "dong", "name": "역삼동"},
        {"type": "road", "name": "테헤란로"},
        {"type": "buildingNumber", "name": "123"}
    ],
    "x": 127028,
    "y": 37356
}
```

## 4. 구현 계획

### 4.1 1단계: Spec 작성
- [x] homecare_matching 기능 분석 및 Spring Boot에서 구현하기 위한 PRD 문서화
- [ ] 각 PRD에서 정의된 큰 기능 단위에 대한 테스트 코드 구현

### 4.2 2단계: 기능 구현
- [ ] 테스트를 통과하는 기능 코드 구현
- [ ] 매칭엔진 API 호출부 코드를 위에서 구현된 함수 호출로 변경
- [ ] 호출지 변경 후 통합 테스트로 정상 동작 여부 검증

### 4.3 3단계: Wrap-up
- [ ] homecare_matching repository에 homecare_BE repository 특정 도메인 부분으로 이관되었다고 README 업데이트
- [ ] homecare_matching 아카이브

## 5. 아키텍처 설계

### 5.1 기존 아키텍처
```
[Client] -> [homecare_BE] -> [REST API] -> [homecare_matching] -> [Database]
```

### 5.2 통합 후 아키텍처
```
[Client] -> [homecare_BE (통합 매칭엔진)] -> [Database]
```

### 5.3 도메인 구조
```
jaega.homecare.domain.match/
├── controller/
│   ├── MatchController.java
│   └── MatchControllerImpl.java
├── service/
│   ├── MatchingService.java (기존 확장)
│   ├── ConditionNormalizationService.java (신규)
│   ├── SchedulingService.java (신규)
│   └── EtaCalculationService.java (신규)
├── infra/
│   ├── OpenRouterClient.java (신규)
│   └── NaverDirectionClient.java (신규)
├── processor/
│   ├── CaregiverFilterProcessor.java (기존 확장)
│   └── LocationFilterProcessor.java (신규)
└── dto/
    ├── req/
    └── res/
```

## 6. 데이터 모델

### 6.1 기존 DTO 유지
- `MatchingRequestDTO`
- `MatchingResponseDTO`
- `ServiceRequestDTO`
- `CaregiverInfo`

### 6.2 (옵셔널) 신규 DTO 추가
실제 해당 DTO와 관련된 기능 구현 시 필요성 여부를 검증해야 합니다:
- `ConditionNormalizationRequest`
- `ConditionNormalizationResponse`
- `EtaCalculationRequest`
- `EtaCalculationResponse`

## 7. 외부 API 연동

### 7.1 OpenRouter API
- **용도**: LLM 기반 근무조건 정규화
- **모델**: Google Gemini 2.5 Flash
- **구현**: Spring WebClient 사용

### 7.2 네이버 Direction API
- **용도**: ETA 계산
- **구현**: Spring WebClient 사용
- **Fallback**: Python에 있던 기존 fallback 코드 제거

### 7.3 Tmap 대중교통 API
- **용도**: 대중교통 이용 시에, ETA 계산
- **구현**: Spring WebClient 사용
- **Fallback**: Python에 있던 기존 fallback 코드 제거

## 8. 테스트 전략

### 8.1 단위 테스트
- 각 서비스 클래스별 단위 테스트
- Mock을 활용한 외부 API 연동 테스트

### 8.2 통합 테스트
- 전체 매칭 플로우 테스트
- 실제 데이터베이스 연동 테스트

### 8.3 성능 테스트
- 매칭 응답 시간 측정
- 동시 요청 처리 성능 테스트

## 9. 마이그레이션 계획

### 9.1 단계적 마이그레이션
1. **Phase 1**: 새로운 매칭 로직 구현 (기존 API와 병행)
2. **Phase 2**: 기존 API 호출을 내부 로직으로 변경
3. **Phase 3**: 레거시 서비스 제거

### 9.2 롤백 계획
- 기존 REST API 호출 방식 유지

## 10. 모니터링 및 로깅

### 10.1 메트릭 수집
- 매칭 요청 수
- 매칭 성공률
- 평균 응답 시간
- 에러 발생률

### 10.2 로깅 전략
- 구조화된 로그 (JSON 형태)
- 매칭 과정별 상세 로그
- 에러 상황 추적 로그

## 11. 보안 고려사항

### 11.1 API 키 관리
- OpenRouter API 키: 환경변수로 관리
- 네이버 API 키: 환경변수로 관리

### 11.2 데이터 보호
- 개인정보 암호화
- 로그에서 민감정보 제거

## 12. 성능 최적화

### 12.1 캐싱 전략
- 요양보호사 목록 캐싱
- ETA 계산 결과 캐싱

### 12.2 비동기 처리
- ETA 계산 비동기 처리
- LLM 호출 비동기 처리

## 13. 배포 전략

### 13.1 헬스체크
- 매칭 서비스 헬스체크 엔드포인트
- 외부 API 연동 상태 확인
