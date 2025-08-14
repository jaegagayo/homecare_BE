package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkLog.repository.WorkLogRepository;
import jaega.homecare.domain.WorkLog.service.command.WorkLogCommandService;
import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
import jaega.homecare.domain.WorkMatch.service.command.WorkMatchCommandService;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.repository.CenterRepository;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.users.entity.*;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CertificationRepository certificationRepository;

    private final ServiceMatchCommandService serviceMatchCommandService;
    private final WorkMatchCommandService workMatchCommandService;
    private final WorkMatchRepository workMatchRepository;
    private final WorkLogRepository workLogRepository;
    private final WorkLogCommandService workLogCommandService;
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

        // 더미 서비스 요청 30개 생성
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

    private void createDummyCaregiver(int index, List<User> caregivers) {
        // 인덱스 13 예외 해결:
        // 파라미터로 넘어온 caregivers 리스트를 사용
        User user = caregivers.get(index);

        Center center = centerRepository.findAll().get(0);

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

        Set<ServiceType> serviceTypes = new HashSet<>();
        serviceTypes.add(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
        if (random.nextBoolean()) {
            serviceTypes.add(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
        }

        Set<DayOfWeek> daysOff = new HashSet<>();
        if (random.nextBoolean()) {
            daysOff.add(DayOfWeek.values()[random.nextInt(7)]);
        }

        Caregiver caregiver = Caregiver.builder()
                .caregiverId(UUID.randomUUID())
                .user(user)
                .availableStartTime(startTime)
                .availableEndTime(endTime)
                .address("서울시 송파구 올림픽로 " + index)
                .location(new Location(37.514 + random.nextDouble() * 0.1, 86.106 + random.nextDouble() * 0.1))
                .serviceTypes(serviceTypes)
                .daysOff(daysOff)
                .build();
        caregiverRepository.save(caregiver);

        CaregiverCenter caregiverCenter = CaregiverCenter.builder()
                .caregiverCenterId(UUID.randomUUID())
                .caregiver(caregiver)
                .center(center)
                .build();
        caregiverCenterRepository.save(caregiverCenter);

        Certification certification = Certification.builder()
                .certificationId(UUID.randomUUID())
                .caregiver(caregiver)
                .certificationNumber("CERT-2025-" + String.format("%04d", index))
                .certificationDate(LocalDate.of(2020 + random.nextInt(5), random.nextInt(12) + 1, random.nextInt(28) + 1))
                .trainStatus(random.nextBoolean())
                .build();
        certificationRepository.save(certification);
    }

    private void createDummyServiceRequest(int index) {
        User user = userRepository.findByUserRole(UserRole.ROLE_CONSUMER).get(random.nextInt(userRepository.findByUserRole(UserRole.ROLE_CONSUMER).size()));

        LocalTime startTime, endTime;
        int timeSlot = random.nextInt(3);
        if (timeSlot == 0) { // 오전 9시 ~ 12시
            startTime = LocalTime.of(9, 0);
            endTime = LocalTime.of(12, 0);
        } else if (timeSlot == 1) { // 오후 1시 ~ 4시
            startTime = LocalTime.of(13, 0);
            endTime = LocalTime.of(16, 0);
        } else { // 오후 5시 ~ 8시
            startTime = LocalTime.of(17, 0);
            endTime = LocalTime.of(20, 0);
        }

        Set<LocalDate> requestedDays = new HashSet<>();
        int pastDaysCount = random.nextInt(3) + 1; // 1~3일 과거
        int futureDaysCount = 1; // 기존 1일 이후
        LocalDate now = LocalDate.now();

        // 과거 날짜 추가 (오늘 기준 6개월 내)
        for (int i = 0; i < pastDaysCount; i++) {
            requestedDays.add(now.minusDays(random.nextInt(30 * 6))); // 6개월 내 과거
        }

        // 미래 날짜 추가
        for (int i = 0; i < futureDaysCount; i++) {
            requestedDays.add(now.plusDays(random.nextInt(2) + 1));
        }

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .address("서울시 강남구 테헤란로 " + index)
                .location(new Location(37.500 + random.nextDouble() * 0.1, 86.037 + random.nextDouble() * 0.1))
                .preferred_time_start(startTime)
                .preferred_time_end(endTime)
                .serviceType(ServiceType.values()[random.nextInt(ServiceType.values().length)])
                .personalityType("친절한")
                .additionalInformation("추가 정보" + index)
                .build();

        UUID serviceRequestId = UUID.randomUUID();
        serviceRequest.setServiceRequest(serviceRequestId, user, ServiceRequestStatus.PENDING, requestedDays);
        serviceRequestRepository.save(serviceRequest);

        List<Caregiver> caregivers = caregiverRepository.findAll();
        if (!caregivers.isEmpty()) {
            Caregiver matchedCaregiver = caregivers.get(random.nextInt(caregivers.size()));
            UUID caregiverId = matchedCaregiver.getCaregiverId();

            // 거리 대충 87~125 사이 랜덤값
            double distanceLog = 87.0 + (random.nextDouble() * 38.0);

            // 서비스 매칭 생성
            CreateServiceMatchRequest createServiceMatchRequest = new CreateServiceMatchRequest(
                    serviceRequestId,
                    caregiverId,
                    startTime,
                    endTime,
                    requestedDays
            );
            serviceMatchCommandService.createServiceMatch(createServiceMatchRequest);

            // 근무 매칭 생성
            CreateWorkMatchRequest createWorkMatchRequest = new CreateWorkMatchRequest(
                    caregiverId,
                    startTime,
                    endTime,
                    requestedDays,
                    serviceRequest.getAddress(),
                    distanceLog
            );
            workMatchCommandService.createWorkMatch(createWorkMatchRequest);

            // WorkMatch 일부를 COMPLETED 상태로 변경
            List<WorkMatch> createdMatches = workMatchRepository.findByCaregiverAndWorkDateIn(
                    matchedCaregiver, requestedDays
            );

            // WorkMatch 상태 랜덤 설정
            for (WorkMatch match : createdMatches) {
                LocalDate workDate = match.getWorkDate();
                if (workDate.isBefore(now)) {
                    // 과거 날짜
                    int statusChoice = random.nextBoolean() ? 1 : 2; // 1: COMPLETED, 2: CANCELLED
                    if (statusChoice == 1) {
                        workMatchCommandService.changeWorkMatchStatus(match.getWorkMatchId(), WorkStatus.COMPLETED);
                        // COMPLETED이면 WorkLog 정산 완료
                        List<WorkLog> logs = workLogRepository.findByWorkMatch(match);
                        for (WorkLog log : logs) {
                            log.togglePaidStatus();
                        }
                    } else {
                        workMatchCommandService.changeWorkMatchStatus(match.getWorkMatchId(), WorkStatus.CANCELLED);
                    }
                } else {
                    // 오늘 이후 날짜
                    workMatchCommandService.changeWorkMatchStatus(match.getWorkMatchId(), WorkStatus.PLANNED);
                }
            }
        }
    }
}