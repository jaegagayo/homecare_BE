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
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
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
    private final RecurringOfferCommandService recurringOfferCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final VoucherCommandService voucherCommandService;
    private final SettlementCommandService settlementCommandService;
    private final Random random = new Random();

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        IntStream.range(0, 87).forEach(this::createDummyUser);

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 생성
        createDummyCenter(0);

        // 3. 더미 요양보호사 생성
        List<User> caregivers = userRepository.findByUserRole(UserRole.ROLE_CAREGIVER);
        createDummyCaregiversWithConditions(caregivers);

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
                "강채영", "조민아", "서지수", "신재혁", "배유림",
                "노지민", "황서현", "문지호", "임지훈", "정예원"
        );

        String name = koreanNames.get(index % koreanNames.size());

        // 이메일 생성: 이름을 영어로 단순 변환 + 인덱스 붙이기
        String emailPrefix = "user" + index;
        String email = emailPrefix + "@dummy.com";

        UserRole role;
        if (index == 0) {
            role = UserRole.ROLE_CENTER; // 0번은 센터
        } else {
            // 나머지는 CAREGIVER 또는 CONSUMER로 분기
            role = (index % 2 == 0) ? UserRole.ROLE_CAREGIVER : UserRole.ROLE_CONSUMER;
        }

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
        center.setCenter(UUID.randomUUID(), user, "순천시재가노인지원센터" + index, "전라남도 순천시 매곡동 1213 ", "061-749-1114" + String.format("%04d", index));
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
                .address(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
                .career(1 + random.nextInt(20)) // 경력 1~20년
                .koreanProficiency(KoreanProficiency.values()[random.nextInt(KoreanProficiency.values().length)])
                .isAccompanyOuting(random.nextBoolean())
                .selfIntroduction(DUMMY_INTRODUCTIONS[index % DUMMY_INTRODUCTIONS.length])
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
        int numDays = 4 + random.nextInt(2); // 1~5일 랜덤
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
                .workArea(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])                // 근무 가능 지역
                .addressType(random.nextBoolean() ? AddressType.ROAD : AddressType.JIBUN) // 랜덤
                .location(new Location(
                        DUMMY_LATITUDES[index % DUMMY_LATITUDES.length],
                        DUMMY_LONGITUDES[index % DUMMY_LONGITUDES.length]
                ))
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

    private void createDummyCaregiversWithConditions(List<User> users) {
        Center center = centerRepository.findAll().get(0);

        // 각 조건별 카운트
        List<Caregiver> dayOfWeekFiltered = new ArrayList<>();
        List<Caregiver> timeFiltered = new ArrayList<>();
        List<Caregiver> areaFiltered = new ArrayList<>();
        List<Caregiver> conditionFiltered = new ArrayList<>();
        List<Caregiver> serviceTypeFiltered = new ArrayList<>();

        for (int index = 0; index < users.size(); index++) {
            User user = users.get(index);

            Set<DayOfWeek> dayOfWeek = new HashSet<>();
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(18, 0);
            String workArea;
            Set<Disease> supportedConditions = new HashSet<>();
            Set<ServiceType> serviceTypes = new HashSet<>();

            // --- 조건별 배정 ---

            if (index < 8) {
                dayOfWeek.addAll(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
            } else {
                dayOfWeek.addAll(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
            }

            if (index >= 8 && index < 15) {
                startTime = LocalTime.of(15, 0);
                endTime = LocalTime.of(18, 0);
            } else {
                startTime = LocalTime.of(9,0);
                endTime = LocalTime.of(12,0);
            }

            if (index >= 15 && index < 21) {
                workArea = pickNonSuncheonAddress(index - 15);
            } else {
                workArea = DUMMY_ADDRESSES[new Random().nextInt(DUMMY_ADDRESSES.length)];
            }

            if (index >= 21 && index < 26) {
                supportedConditions.add(Disease.HYPERTENSION); // 조건 없는 사람
            } else {
                supportedConditions.add(Disease.DEMENTIA);
                supportedConditions.add(Disease.BEDRIDDEN);
            }

            if (index >= 26 && index < 33) {
                serviceTypes.add(ServiceType.VISITING_CARE); // 다른 유형
            } else {
                serviceTypes.add(ServiceType.IN_HOME_SUPPORT);
            }

            // --- Caregiver 생성 ---
            Caregiver caregiver = Caregiver.builder()
                    .caregiverId(UUID.randomUUID())
                    .user(user)
                    .address(workArea)
                    .career(1 + new Random().nextInt(20))
                    .koreanProficiency(KoreanProficiency.values()[new Random().nextInt(KoreanProficiency.values().length)])
                    .isAccompanyOuting(new Random().nextBoolean())
                    .selfIntroduction(DUMMY_INTRODUCTIONS[index % DUMMY_INTRODUCTIONS.length])
                    .build();
            caregiver.changeVerifiedStatus(VerifiedStatus.APPROVED);
            caregiverRepository.save(caregiver);

            // CaregiverCenter 생성
            CaregiverCenter caregiverCenter = CaregiverCenter.builder()
                    .caregiverCenterId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .center(center)
                    .status(CaregiverStatus.values()[new Random().nextInt(CaregiverStatus.values().length)])
                    .build();
            caregiverCenterRepository.save(caregiverCenter);

            // CaregiverPreference 생성
            CaregiverPreference preference = CaregiverPreference.builder()
                    .caregiverPreferenceId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .serviceTypes(serviceTypes)
                    .dayOfWeek(dayOfWeek)
                    .workStartTime(startTime)
                    .workEndTime(endTime)
                    .workMinTime(2 + new Random().nextInt(2))
                    .workMaxTime(4 + new Random().nextInt(4))
                    .availableTime(30 + new Random().nextInt(91))
                    .workArea(workArea)
                    .addressType(new Random().nextBoolean() ? AddressType.ROAD : AddressType.JIBUN)
                    .location(new Location(DUMMY_LATITUDES[index % DUMMY_LATITUDES.length],
                            DUMMY_LONGITUDES[index % DUMMY_LONGITUDES.length]))
                    .transportation(new Random().nextBoolean() ? "자가차량" : "대중교통")
                    .lunchBreak(30)
                    .bufferTime(15)
                    .supportedConditions(supportedConditions)
                    .preferredMinAge(40 + new Random().nextInt(20))
                    .preferredMaxAge(60 + new Random().nextInt(20))
                    .preferredGender(PreferredGender.values()[new Random().nextInt(PreferredGender.values().length)])
                    .build();
            caregiverPreferenceRepository.save(preference);

            // Certification 생성
            Certification certification = Certification.builder()
                    .certificationId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .certificationNumber("CERT-2025-" + String.format("%04d", index))
                    .certificationDate(LocalDate.of(2020 + new Random().nextInt(5),
                            1 + new Random().nextInt(12),
                            1 + new Random().nextInt(28)))
                    .trainStatus(new Random().nextBoolean())
                    .build();
            certificationRepository.save(certification);

            // --- 조건별 필터링 로그 ---
            if (dayOfWeek.size() == 5) dayOfWeekFiltered.add(caregiver);
            if (!(startTime.equals(LocalTime.of(9,0)) && endTime.equals(LocalTime.of(12,0))) ) timeFiltered.add(caregiver);
            if (!workArea.startsWith("전라남도 순천시")) areaFiltered.add(caregiver);
            if (supportedConditions.contains(Disease.HYPERTENSION)) conditionFiltered.add(caregiver);
            if (serviceTypes.contains(ServiceType.VISITING_CARE)) serviceTypeFiltered.add(caregiver);
        }

        // --- 출력 ---
        System.out.println("근무 요일 월~금 아닌 사람: " + dayOfWeekFiltered.size());
        System.out.println("근무 시간이 9~12가 아닌 사람: " + timeFiltered.size());
        System.out.println("근무 지역 순천 외 사람: " + areaFiltered.size());
        System.out.println("지원 가능한 상태 조건이 없는 사람: " + conditionFiltered.size());
        System.out.println("서비스 유형 VISITING_CARE인 사람: " + serviceTypeFiltered.size());
    }

    private void createDummyConsumer(int index, List<User> consumers) {
        User user = consumers.get(index);

        Consumer consumer = Consumer.builder()
                .user(user)
                .consumerId(UUID.randomUUID())
                .residentialAddress(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
                .visitAddress(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
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

    private UUID createDummyRecurringOfferForConsumer(Consumer consumer) {
        List<Caregiver> approvedCaregivers = caregiverRepository.findAll();

        if (approvedCaregivers.isEmpty()) return null;

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
        return offer.getRecurringOfferId();
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
                .serviceAddress(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
                .addressType(random.nextBoolean() ? AddressType.ROAD : AddressType.JIBUN) // 랜덤
                .location(new Location(
                        DUMMY_LATITUDES[index % DUMMY_LATITUDES.length],
                        DUMMY_LONGITUDES[index % DUMMY_LONGITUDES.length]
                ))
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
        // 전체 ACTIVE 요양보호사 조회
        List<Caregiver> activeCaregivers = caregiverCenterRepository.findByStatus(CaregiverStatus.ACTIVE)
                .stream()
                .map(CaregiverCenter::getCaregiver)
                .distinct()
                .limit(10) // 앞 10명만
                .toList();

        if (activeCaregivers.isEmpty()) return;

        // 날짜 범위: 오늘 기준 -4일 ~ +4일
        List<LocalDate> possibleDates = IntStream.rangeClosed(-4, 4)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .filter(d -> !(d.getYear() == 2025 && d.getMonthValue() == 9 && d.getDayOfMonth() == 5)) // 9월 5일 제외
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

                // 랜덤으로 매칭할 요양보호사 선택
                Caregiver caregiver = activeCaregivers.get(random.nextInt(activeCaregivers.size()));

                // 중복 체크
                if (serviceMatchQueryRepository.existsByCaregiverAndDateTime(caregiver.getCaregiverId(), date, startTime, endTime)) {
                    continue; // 이미 겹치면 스킵
                }

                // ServiceRequest 생성
                ServiceRequest serviceRequest = ServiceRequest.builder()
                        .consumer(consumer)
                        .serviceAddress(consumer.getResidentialAddress())
                        .addressType(AddressType.ROAD)
                        .location(new Location(34.9485, 127.4942))
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

                // 상태 결정
                if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                    serviceMatch.changeMatchStatus(MatchStatus.CONFIRMED);
                } else {
                    serviceMatch.changeMatchStatus(MatchStatus.COMPLETED);

                    if (random.nextBoolean()) {
                        // COMPLETED이면 리뷰 생성
                        Review review = Review.builder()
                                .reviewId(UUID.randomUUID())
                                .serviceMatch(serviceMatch)
                                .reviewScore(5.0)
                                .reviewContent("테스트용 리뷰")
                                .build();
                        reviewRepository.save(review);
                    }
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

        LocalTime preferredStartTime = LocalTime.of(13, 0);
        LocalTime preferredEndTime = LocalTime.of(16, 0);


        ServiceRequest serviceRequest = ServiceRequest.builder()
                .consumer(consumer)
                .serviceAddress("전남 순천시 성동3길 8")
                .addressType(AddressType.ROAD)
                .location(new Location(34.9485, 127.4942))
                .requestDate(LocalDate.of(2025, 9, 5))
                .preferredStartTime(preferredStartTime)
                .preferredEndTime(preferredEndTime)
                .duration((int) Duration.between(preferredStartTime, preferredEndTime).toHours())
                .serviceType(ServiceType.values()[0])
                .additionalInformation("전시용 데이터")
                .build();

        UUID serviceRequestId = UUID.randomUUID();
        serviceRequest.initializeServiceRequest(serviceRequestId);
        serviceRequestRepository.save(serviceRequest);

        UUID recurringOfferId = createDummyRecurringOfferForConsumer(consumer);

        recurringOfferCommandService.approveRecurringStatus(recurringOfferId);
    }

    private static final String[] DUMMY_ADDRESSES = {
            "전라남도 순천시 성동3길 5",
            "전라남도 순천시 성동3길 8",
            "전라남도 순천시 성동2길 12",
            "전라남도 순천시 조례동 45",
            "전라남도 순천시 왕지동 78",
            "전라남도 순천시 해룡면 신기리 34",
            "전라남도 순천시 연향동 123",
            "전라남도 순천시 매곡동 56",
            "전라남도 순천시 왕조2길 67",
            "전라남도 순천시 풍덕동 89",
            "전라남도 순천시 상사동 11",
            "전라남도 순천시 조례동 12",
            "전라남도 순천시 왕지동 34",
            "전라남도 순천시 해룡면 신기리 56",
            "전라남도 순천시 연향동 78",
            "전라남도 순천시 매곡동 90",
            "전라남도 순천시 왕조2길 45",
            "전라남도 순천시 풍덕동 67",
            "전라남도 순천시 상사동 23",
            "전라남도 순천시 조례동 89",
            "전라남도 순천시 왕지동 12",
            "전라남도 순천시 해룡면 신기리 78",
            "전라남도 순천시 연향동 34",
            "전라남도 순천시 매곡동 12",
            "전라남도 순천시 왕조2길 89",
            "전라남도 순천시 풍덕동 34",
            "전라남도 순천시 상사동 45",
            "전라남도 순천시 조례동 23",
            "전라남도 순천시 왕지동 56",
            "전라남도 순천시 해룡면 신기리 12"
    };

    private static final double[] DUMMY_LATITUDES = {
            34.9485, 34.9486, 34.9487, 34.9574, 34.9532, 34.9316, 34.9516, 34.9378, 34.9512, 34.9310,
            34.9510, 34.9570, 34.9530, 34.9318, 34.9515, 34.9379, 34.9511, 34.9312, 34.9512, 34.9572,
            34.9531, 34.9319, 34.9514, 34.9377, 34.9513, 34.9311, 34.9511, 34.9571, 34.9533, 34.9317
    };

    private static final double[] DUMMY_LONGITUDES = {
            127.4942, 127.4945, 127.4935, 127.5206, 127.4968, 127.4662, 127.5188, 127.4745, 127.4879, 127.4973,
            127.4852, 127.5200, 127.4965, 127.4665, 127.5185, 127.4748, 127.4875, 127.4970, 127.4855, 127.5208,
            127.4967, 127.4668, 127.5186, 127.4746, 127.4878, 127.4971, 127.4853, 127.5204, 127.4969, 127.4669
    };

    private static final String[] DUMMY_INTRODUCTIONS = {
            "안녕하세요! 따뜻한 마음으로 어르신을 돌보며 정성껏 케어하겠습니다.",
            "안녕하세요! 밝은 미소로 어르신과 소통하며 케어하겠습니다.",
            "안녕하세요! 건강하고 즐거운 일상을 위해 최선을 다하겠습니다.",
            "안녕하세요! 어르신의 안전과 행복을 최우선으로 생각하며 케어하겠습니다.",
            "안녕하세요! 세심한 배려로 편안한 돌봄을 제공하겠습니다.",
            "안녕하세요! 항상 친절하고 책임감 있게 케어하겠습니다.",
            "안녕하세요! 어르신의 생활 만족도를 높이는 케어를 하겠습니다.",
            "안녕하세요! 어르신과 가족의 신뢰를 소중히 여기며 돌봄을 제공하겠습니다.",
            "안녕하세요! 즐거운 마음과 따뜻한 손길로 케어하겠습니다.",
            "안녕하세요! 전문적이고 성실한 돌봄을 약속드립니다."
    };

    // 예시: 순천 외 주소 선택 함수
    private String pickNonSuncheonAddress(int index) {
        String[] otherAddresses = {
                "경기도 수원시 팔달구 매산로 1",
                "전라북도 전주시 완산구 전동 3",
                "충청북도 청주시 상당구 사직로 5",
                "충청남도 천안시 동남구 중앙로 12",
                "경상북도 포항시 남구 대이로 45",
                "경상남도 창원시 성산구 중앙대로 34"
        };
        return otherAddresses[index % otherAddresses.length];
    }
}
