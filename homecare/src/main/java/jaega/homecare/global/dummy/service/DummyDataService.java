package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.entity.KoreanProficiency;
import jaega.homecare.domain.caregiver.entity.VerifiedStatus;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.entity.PreferredGender;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.repository.CenterRepository;
import jaega.homecare.domain.consumer.entity.CognitiveStatus;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.review.repository.ReviewRepository;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.users.entity.*;
import jaega.homecare.domain.users.repository.UserRepository;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.repository.VoucherRepository;
import jaega.homecare.domain.voucher.service.command.VoucherCommandService;
import jaega.homecare.domain.voucher.service.query.VoucherQueryService;
import jaega.homecare.domain.voucherUsage.service.command.VoucherUsageCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CaregiverRepository caregiverRepository;
    private final ConsumerRepository consumerRepository;
    private final ReviewRepository reviewRepository;
    private final VoucherRepository voucherRepository;
    private final RecurringOfferRepository recurringOfferRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CertificationRepository certificationRepository;
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;

    private final VoucherQueryService voucherQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final VoucherUsageCommandService voucherUsageCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final VoucherCommandService voucherCommandService;
    private final SettlementCommandService settlementCommandService;
    private final Random random = new Random();

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        IntStream.range(0, 100).forEach(this::createDummyUser);

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 5개 생성
        createDummyCenter(0);

        // 3. 더미 요양보호사 생성
        List<User> caregivers = userRepository.findByUserRole(UserRole.ROLE_CAREGIVER);
        IntStream.range(0, caregivers.size()).forEach(index -> createDummyCaregiver(index, caregivers));

        // 4. Consumer 생성
        List<User> consumers = userRepository.findByUserRole(UserRole.ROLE_CONSUMER);
        IntStream.range(0, consumers.size()).forEach(index -> createDummyConsumer(index, consumers));

        // 4-1. dummy1@user.com 전용 Consumer 보장 생성
        User dummyUser = userRepository.findByEmail("user1@dummy.com");
        Consumer dummyConsumer = consumerRepository.findByUser(dummyUser);

        // 전용 ServiceRequest / ServiceMatch / VoucherUsage / Review 생성
        createDummyServiceRequestForConsumer(dummyConsumer);

        // 5. ServiceRequest 생성 (Consumer 기반)
        IntStream.range(0, 30).forEach(this::createDummyServiceRequest);
    }

    private void createDummyUser(int index) {
        List<String> koreanNames = List.of(
                "김기현", "박지성", "이재민", "최유진", "장서연",
                "정하늘", "김소희", "이수환", "박민재", "윤지영",
                "한동훈", "강채영", "오세훈", "조민아", "서지수",
                "신재혁", "배유림", "노지민", "황서현", "문지호"
        );

        String name = koreanNames.get(index % koreanNames.size());

        // 이메일 생성: 이름을 영어로 단순 변환 + 인덱스 붙이기
        String emailPrefix = "user" + index;
        String email = emailPrefix + "@dummy.com";

        UserRole role = index % 5 == 0 ? UserRole.ROLE_CENTER :
                (index % 3 == 0 ? UserRole.ROLE_CAREGIVER : UserRole.ROLE_CONSUMER);

        User user = User.builder()
                .name(name)
                .email(email)
                .password("$2a$10$vvUzhakZH7BQ0fpo8RfS/u3Ip54VLNHAQSoBCnCIYKSxVBmAhxaVG")
                .phone("010-1234-" + String.format("%04d", index))
                .birthDate(LocalDate.of(1970 + random.nextInt(30), random.nextInt(12) + 1, random.nextInt(28) + 1))
                .gender(Gender.values()[random.nextInt(Gender.values().length)])
                .build();

        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());
        userRepository.save(user);
    }

    private void createDummyCenter(int index) {
        User user = userRepository.findByUserRole(UserRole.ROLE_CENTER).get(index);
        Center center = new Center();
        center.setCenter(UUID.randomUUID(), user, "더미센터" + index, "서울시 강남구 테헤란로 " + index, "02-1111-" + String.format("%04d", index));
        centerRepository.save(center);
    }

    private void createDummyCaregiver(int index, List<User> users) {
        User user = users.get(index);
        Center center = centerRepository.findAll().get(0);

        // 근무 시간 랜덤 생성
        LocalTime startTime, endTime;
        int timeSlot = random.nextInt(3);
        if (timeSlot == 0) {
            startTime = LocalTime.of(9, 0);
            endTime = LocalTime.of(18, 0);
        } else if (timeSlot == 1) {
            startTime = LocalTime.of(7, 0);
            endTime = LocalTime.of(16, 0);
        } else {
            startTime = LocalTime.of(8, 0);
            endTime = LocalTime.of(20, 0);
        }

        // Caregiver 생성
        Caregiver caregiver = Caregiver.builder()
                .caregiverId(UUID.randomUUID())
                .user(user)
                .address("서울시 송파구 올림픽로 " + index)
                .career(1 + random.nextInt(20)) // 경력 1~20년
                .koreanProficiency(KoreanProficiency.values()[random.nextInt(KoreanProficiency.values().length)])
                .isAccompanyOuting(random.nextBoolean())
                .selfIntroduction("안녕하세요! 요양보호사 " + user.getName() + "입니다.")
                .build();
        caregiver.changeVerifiedStatus(VerifiedStatus.APPROVED);
        caregiverRepository.save(caregiver);

        // CaregiverCenter 생성 (상태 랜덤)
        CaregiverStatus status;
        if (index == 0) {
            status = CaregiverStatus.ACTIVE; // 첫 번째는 무조건 ACTIVE
        } else {
            status = CaregiverStatus.values()[random.nextInt(CaregiverStatus.values().length)];
        }

        CaregiverCenter caregiverCenter = CaregiverCenter.builder()
                .caregiverCenterId(UUID.randomUUID())
                .caregiver(caregiver)
                .center(center)
                .status(status)
                .build();
        caregiverCenterRepository.save(caregiverCenter);

        // 서비스 타입 랜덤
        Set<ServiceType> serviceTypes = new HashSet<>();
        serviceTypes.add(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
        if (random.nextBoolean()) {
            serviceTypes.add(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
        }

        // 근무 가능 요일 랜덤
        Set<DayOfWeek> dayOfWeek = new HashSet<>();
        int numDays = 1 + random.nextInt(5); // 1~5일 랜덤
        while (dayOfWeek.size() < numDays) {
            dayOfWeek.add(DayOfWeek.values()[random.nextInt(7)]);
        }

        // 지원 가능 질환 랜덤
        Set<Disease> supportedConditions = new HashSet<>();
        if (random.nextBoolean()) supportedConditions.add(Disease.DEMENTIA); // 치매
        if (random.nextBoolean()) supportedConditions.add(Disease.BEDRIDDEN); // 와상

        // CaregiverPreference 생성
        CaregiverPreference preference = CaregiverPreference.builder()
                .caregiverPreferenceId(UUID.randomUUID())
                .caregiver(caregiver)
                .serviceTypes(serviceTypes)
                .dayOfWeek(dayOfWeek)
                .workStartTime(startTime)
                .workEndTime(endTime)
                .workMinTime(2 + random.nextInt(2))      // 최소 근무시간 2~3시간
                .workMaxTime(4 + random.nextInt(4))      // 최대 근무시간 4~7시간
                .availableTime(30 + random.nextInt(91))  // 이동 가능 시간 30~120분
                .workArea("서울시 송파구")                // 근무 가능 지역
                .transportation(random.nextBoolean() ? "자가차량" : "대중교통")
                .lunchBreak(30)                           // 점심시간 30분
                .bufferTime(15)                           // 이동 시간 제외 버퍼 15분
                .supportedConditions(supportedConditions)
                .preferredMinAge(40 + random.nextInt(20))  // 선호 최소 연령 40~59
                .preferredMaxAge(60 + random.nextInt(20))  // 선호 최대 연령 60~79
                .preferredGender(PreferredGender.values()[random.nextInt(PreferredGender.values().length)])
                .build();

        caregiverPreferenceRepository.save(preference);

        // Certification 생성 (기존 유지)
        Certification certification = Certification.builder()
                .certificationId(UUID.randomUUID())
                .caregiver(caregiver)
                .certificationNumber("CERT-2025-" + String.format("%04d", index))
                .certificationDate(LocalDate.of(2020 + random.nextInt(5),
                        1 + random.nextInt(12),
                        1 + random.nextInt(28)))
                .trainStatus(random.nextBoolean())
                .build();
        certificationRepository.save(certification);
    }

    private void createDummyConsumer(int index, List<User> consumers) {
        User user = consumers.get(index);

        Consumer consumer = Consumer.builder()
                .user(user)
                .consumerId(UUID.randomUUID())
                .residentialAddress("서울시 마포구 월드컵북로 " + index)
                .visitAddress("서울시 강남구 봉은사로 " + index)
                .entranceType("공동현관 비밀번호: " + (1000 + random.nextInt(9000)))
                .careGrade(random.nextInt(6) + 1)
                .isMedicalAid(random.nextBoolean())
                .weight(40 + random.nextInt(40)) // 40~80kg
                .disease(Disease.values()[random.nextInt(Disease.values().length)])
                .cognitiveStatus(CognitiveStatus.values()[random.nextInt(CognitiveStatus.values().length)])
                .livingSituation("혼자 거주")
                .guardianName("보호자" + index)
                .guardianPhone("010-9999-" + String.format("%04d", index))
                .build();

        consumer.initializeConsumer(UUID.randomUUID());
        consumerRepository.save(consumer);

        // ✅ Consumer 생성 시 Voucher 생성
        Long totalAmount = voucherCommandService.getTotalAmountByCareGrade(consumer.getCareGrade());

        Voucher voucher = Voucher.builder()
                .voucherId(UUID.randomUUID())
                .consumer(consumer)
                .voucherDate(LocalDate.now()) // 이번 달 바우처
                .totalAmount(totalAmount)
                .build();

        voucherRepository.save(voucher);

        createDummyRecurringOfferForConsumer(consumer);
    }

    private void createDummyRecurringOfferForConsumer(Consumer consumer) {
        List<Caregiver> approvedCaregivers = caregiverRepository.findAll();

        if (approvedCaregivers.isEmpty()) return;

        Caregiver caregiver = approvedCaregivers.get(random.nextInt(approvedCaregivers.size()));

        // 근무 요일 랜덤
        Set<DayOfWeek> dayOfWeek = new HashSet<>();
        int numDays = 1 + random.nextInt(5);
        while (dayOfWeek.size() < numDays) {
            dayOfWeek.add(DayOfWeek.values()[random.nextInt(7)]);
        }

        LocalTime startTime = LocalTime.of(9 + random.nextInt(8), 0);
        LocalTime endTime = startTime.plusHours(3 + random.nextInt(3));

        LocalDate startDate = LocalDate.now().plusDays(random.nextInt(30));
        LocalDate endDate = startDate.plusWeeks(4 + random.nextInt(8));

        ServiceType serviceType = ServiceType.values()[random.nextInt(ServiceType.values().length)];

        RecurringOffer offer = RecurringOffer.builder()
                .recurringOfferId(UUID.randomUUID())
                .consumer(consumer)
                .caregiver(caregiver)
                .serviceAddress(consumer.getResidentialAddress())
                .addressType(AddressType.ROAD)
                .location(new Location(37.500 + random.nextDouble() * 0.1, 126.970 + random.nextDouble() * 0.1))
                .dayOfWeek(dayOfWeek)
                .serviceStartDate(startDate)
                .serviceEndDate(endDate)
                .serviceStartTime(startTime)
                .serviceEndTime(endTime)
                .serviceType(serviceType)
                .recurringStatus(RecurringStatus.PENDING)
                .recurringOfferUnread(true)
                .build();

        recurringOfferRepository.save(offer);
    }

    private void createDummyServiceRequest(int index) {
        // ✅ Consumer 중 하나 선택
        List<Consumer> consumers = consumerRepository.findAll();
        Consumer consumer = consumers.get(random.nextInt(consumers.size()));

        // ✅ 서비스 시간 랜덤 지정
        LocalTime serviceStartTime, serviceEndTime;
        int timeSlot = random.nextInt(3);
        if (timeSlot == 0) {
            serviceStartTime = LocalTime.of(9, 0);
            serviceEndTime = LocalTime.of(12, 0);
        } else if (timeSlot == 1) {
            serviceStartTime = LocalTime.of(13, 0);
            serviceEndTime = LocalTime.of(16, 0);
        } else {
            serviceStartTime = LocalTime.of(17, 0);
            serviceEndTime = LocalTime.of(20, 0);
        }

        // ✅ 요청 날짜 → +-2일로 생성되도록 변경
        LocalDate requestedDate = LocalDate.now().plusDays(random.nextInt(5) - 2);

        // 3. Duration 계산 (시간 차)
        int duration = (int) java.time.Duration.between(serviceStartTime, serviceEndTime).toHours();

        // ✅ 서비스 요청 생성
        ServiceRequest serviceRequest = ServiceRequest.builder()
                .consumer(consumer)
                .serviceAddress("서울시 강남구 테헤란로 " + index)
                .addressType(random.nextBoolean() ? AddressType.ROAD : AddressType.JIBUN) // 랜덤
                .location(new Location(37.500 + random.nextDouble() * 0.1, 126.970 + random.nextDouble() * 0.1)) // ✅ 위도/경도 수정
                .requestDate(requestedDate)
                .preferredStartTime(serviceStartTime)
                .preferredEndTime(serviceEndTime)
                .duration(duration)
                .serviceType(ServiceType.values()[random.nextInt(ServiceType.values().length)])
                .additionalInformation("추가 정보" + index)
                .build();

        UUID serviceRequestId = UUID.randomUUID();
        serviceRequest.initializeServiceRequest(serviceRequestId);
        serviceRequestRepository.save(serviceRequest);

        // ✅ ACTIVE 상태 요양보호사 조회
        List<Caregiver> activeCaregivers = caregiverCenterRepository.findByStatus(CaregiverStatus.ACTIVE)
                .stream()
                .map(CaregiverCenter::getCaregiver)
                .toList();

        if (!activeCaregivers.isEmpty()) {
            Caregiver matchedCaregiver = activeCaregivers.get(random.nextInt(activeCaregivers.size()));
            UUID caregiverId = matchedCaregiver.getCaregiverId();

            // 2. 해당 요양보호사의 CaregiverCenter 리스트 가져오기
            List<CaregiverCenter> caregiverCenters = caregiverCenterRepository.findByCaregiver_CaregiverId(caregiverId);

            if (caregiverCenters.isEmpty()) {
                return; // 연결된 센터가 없으면 스킵
            }


            // 3. 랜덤으로 하나 선택
            CaregiverCenter selectedCaregiverCenter = caregiverCenters.get(random.nextInt(caregiverCenters.size()));

            // ✅ 거리 랜덤 생성
            double distanceLog = 87.0 + (random.nextDouble() * 38.0);

            // ✅ 서비스 매칭 생성
            CreateServiceMatchRequest createServiceMatchRequest = new CreateServiceMatchRequest(
                    serviceRequestId,
                    caregiverId,
                    serviceStartTime,
                    serviceEndTime,
                    requestedDate // ✅ 단일 날짜로 변경
            );

            // ✅ 매칭 생성 후 반환값에서 serviceMatchId 가져오기
            UUID serviceMatchId = serviceMatchCommandService.createServiceMatch(createServiceMatchRequest);


            // ✅ 일정 상태를 랜덤으로 CONFIRMED / CONFIRMED
            // ✅ 일정 상태 결정: 날짜 기준
            ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);
            MatchStatus matchStatus;
            if (requestedDate.isAfter(LocalDate.now())) {
                matchStatus = MatchStatus.CONFIRMED;
            } else {
                matchStatus = MatchStatus.COMPLETED;
            }
            serviceMatch.changeMatchStatus(matchStatus);

            // COMPLETED인 경우에만 리뷰 생성
            if (matchStatus == MatchStatus.COMPLETED) {
                Review review = Review.builder()
                        .reviewId(UUID.randomUUID())
                        .serviceMatch(serviceMatch)
                        .reviewScore(1.0 + (4.0 * random.nextDouble())) // 1~5점 랜덤
                        .reviewContent("더미 리뷰 내용입니다.")
                        .build();

                reviewRepository.save(review);
            }

            // ✅ ServiceRequest 상태 동기화
            ServiceRequest match_serviceRequest = serviceMatch.getServiceRequest();
            match_serviceRequest.changeRequestStatus(ServiceRequestStatus.ASSIGNED);


            // 정산 생성
            CreateSettlementRequest createSettlementRequest = new CreateSettlementRequest(
                    selectedCaregiverCenter.getCaregiverCenterId(),
                    serviceMatchId,
                    distanceLog
            );
            settlementCommandService.createSettlement(createSettlementRequest);
        }
    }

    private void createDummyServiceRequestForConsumer(Consumer consumer) {
        // user3@dummy.com 요양보호사 조회
        User user = userRepository.findByEmail("user3@dummy.com");
        if (user == null) return;

        Caregiver caregiver = caregiverRepository.findByUser(user);
        if (caregiver == null) return;

        // 날짜 범위: 오늘 기준 -4일 ~ +4일
        List<LocalDate> possibleDates = IntStream.rangeClosed(-4, 4)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .toList();

        // 시간대
        List<LocalTime[]> timeSlots = List.of(
                new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(12, 0)},
                new LocalTime[]{LocalTime.of(13, 0), LocalTime.of(16, 0)},
                new LocalTime[]{LocalTime.of(17, 0), LocalTime.of(20, 0)}
        );

        for (LocalDate date : possibleDates) {
            for (LocalTime[] slot : timeSlots) {
                LocalTime startTime = slot[0];
                LocalTime endTime = slot[1];

                // 중복 체크
                if (serviceMatchQueryRepository.existsByCaregiverAndDateTime(caregiver.getCaregiverId(), date, startTime, endTime)) {
                    continue; // 이미 겹치면 스킵
                }

                // ServiceRequest 생성
                ServiceRequest serviceRequest = ServiceRequest.builder()
                        .consumer(consumer)
                        .serviceAddress(consumer.getResidentialAddress())
                        .addressType(AddressType.ROAD)
                        .location(new Location(37.500, 126.970))
                        .requestDate(date)
                        .preferredStartTime(startTime)
                        .preferredEndTime(endTime)
                        .duration((int) Duration.between(startTime, endTime).toHours())
                        .serviceType(ServiceType.values()[0])
                        .additionalInformation("테스트용 서비스 요청")
                        .build();

                UUID serviceRequestId = UUID.randomUUID();
                serviceRequest.initializeServiceRequest(serviceRequestId);
                serviceRequestRepository.save(serviceRequest);

                // ServiceMatch 생성
                CreateServiceMatchRequest matchRequest = new CreateServiceMatchRequest(
                        serviceRequestId,
                        caregiver.getCaregiverId(),
                        startTime,
                        endTime,
                        date
                );
                UUID serviceMatchId = serviceMatchCommandService.createServiceMatch(matchRequest);

                ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);

                // 상태: 오늘 이후면 CONFIRMED, 이전이면 COMPLETED
                if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                    serviceMatch.changeMatchStatus(MatchStatus.CONFIRMED);
                } else {
                    serviceMatch.changeMatchStatus(MatchStatus.COMPLETED);

                    // COMPLETED이면 리뷰 생성
                    Review review = Review.builder()
                            .reviewId(UUID.randomUUID())
                            .serviceMatch(serviceMatch)
                            .reviewScore(5.0)
                            .reviewContent("테스트용 리뷰")
                            .build();
                    reviewRepository.save(review);
                }

                // ServiceRequest 상태 동기화
                serviceRequest.changeRequestStatus(ServiceRequestStatus.ASSIGNED);

                // Settlement 생성
                CreateSettlementRequest settlementRequest = new CreateSettlementRequest(
                        caregiverCenterRepository.findByCaregiver_CaregiverId(caregiver.getCaregiverId()).get(0).getCaregiverCenterId(),
                        serviceMatchId,
                        100.0
                );
                settlementCommandService.createSettlement(settlementRequest);
            }
        }
    }
}