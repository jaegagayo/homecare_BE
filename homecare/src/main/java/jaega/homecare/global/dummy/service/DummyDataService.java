package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.service.command.WorkMatchCommandService;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
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
    private final ServiceRequestRepository serviceRequestRepository;
    private final CertificationRepository certificationRepository;

    private final ServiceMatchCommandService serviceMatchCommandService;
    private final WorkMatchCommandService workMatchCommandService;
    private final Random random = new Random();

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        IntStream.range(0, 60).forEach(this::createDummyUser);

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 5개 생성
        IntStream.range(0, 3).forEach(this::createDummyCenter);

        // 더미 요양보호사 생성
        List<User> caregivers = userRepository.findByUserRole(UserRole.ROLE_CAREGIVER);
        IntStream.range(0, caregivers.size()).forEach(index -> createDummyCaregiver(index, caregivers));

        // 더미 서비스 요청 30개 생성
        IntStream.range(0, 30).forEach(this::createDummyServiceRequest);
    }

    private void createDummyUser(int index) {
        UserRole role = index % 5 == 0 ? UserRole.ROLE_CENTER : (index % 3 == 0 ? UserRole.ROLE_CAREGIVER : UserRole.ROLE_CONSUMER);
        User user = User.builder()
                .name("더미유저" + index)
                .email("user" + index + "@dummy.com")
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

        Center center = centerRepository.findAll().get(random.nextInt(3));

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
                .center(center)
                .availableStartTime(startTime)
                .availableEndTime(endTime)
                .address("서울시 송파구 올림픽로 " + index)
                .location(new Location(37.514 + random.nextDouble() * 0.1, 86.106 + random.nextDouble() * 0.1))
                .serviceTypes(serviceTypes)
                .daysOff(daysOff)
                .build();
        caregiverRepository.save(caregiver);

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
        int daysToRequest = 1;
        for (int i = 0; i < daysToRequest; i++) {
            requestedDays.add(LocalDate.now().plusDays(random.nextInt(2) + 1));
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
        }
    }
}