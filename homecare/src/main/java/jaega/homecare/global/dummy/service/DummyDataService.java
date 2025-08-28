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
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.repository.SettlementRepository;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.users.entity.*;
import jaega.homecare.domain.users.repository.UserRepository;
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
    private final SettlementRepository settlementRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CertificationRepository certificationRepository;
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;

    private final ServiceMatchCommandService serviceMatchCommandService;
    private final SettlementCommandService settlementCommandService;
    private final Random random = new Random();

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        IntStream.range(0, 100).forEach(this::createDummyUser);

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 5개 생성
        createDummyCenter(0);

        // 더미 요양보호사 생성
        List<User> caregivers = userRepository.findByUserRole(UserRole.ROLE_CAREGIVER);
        IntStream.range(0, caregivers.size()).forEach(index -> createDummyCaregiver(index, caregivers));


        // ✅ 4. Consumer 생성
        List<User> consumers = userRepository.findByUserRole(UserRole.ROLE_CONSUMER);
        IntStream.range(0, consumers.size()).forEach(index -> createDummyConsumer(index, consumers));

        // ✅ 5. ServiceRequest 생성 (Consumer 기반)
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
                .verifiedStatus(VerifiedStatus.PENDING)
                .build();
        caregiverRepository.save(caregiver);

        // CaregiverCenter 생성 (상태 랜덤)
        CaregiverStatus status = CaregiverStatus.values()[random.nextInt(CaregiverStatus.values().length)];
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
                .careGrade(random.nextInt(5) + 1)
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

        // ✅ 요청 날짜 → 한 개만 랜덤으로 설정 (미래 날짜)
        LocalDate requestedDate = LocalDate.now().plusDays(random.nextInt(3) + 1);

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

            // ✅ 정산 생성
            CreateSettlementRequest createSettlementRequest = new CreateSettlementRequest(
                    selectedCaregiverCenter.getCaregiverCenterId(),
                    serviceMatchId,
                    distanceLog
            );
            settlementCommandService.createSettlement(createSettlementRequest);
        }
    }
}