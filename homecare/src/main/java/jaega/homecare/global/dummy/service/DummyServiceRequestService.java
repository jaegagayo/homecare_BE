package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
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
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static jaega.homecare.global.dummy.service.DummyData.*;

@Service
@RequiredArgsConstructor
public class DummyServiceRequestService {

    private final ConsumerRepository consumerRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final SettlementCommandService settlementCommandService;
    private final SettlementQueryService settlementQueryService;
    private final RecurringOfferCommandService recurringOfferCommandService;

    private final Random random = new Random();

    private final DummyRecurringOfferService dummyRecurringOfferService;

    public void generateDummyServiceRequest(){
        IntStream.range(0, 30).forEach(this::createDummyServiceRequest);
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
        List<CaregiverCenter> activeCaregiverCenters = caregiverCenterRepository.findByStatus(CaregiverStatus.ACTIVE);

        if (activeCaregiverCenters.isEmpty()) return;

        // --- 1️⃣ 첫 번째 요양보호사 확정 생성 ---
        createDummyServiceRequestForCaregiver(activeCaregiverCenters.get(0), serviceRequest, requestedDate, serviceStartTime, serviceEndTime,
                87.0 + (random.nextDouble() * 38.0));

        // --- 2️⃣ 나머지 요양보호사는 랜덤 생성 ---
        for (int i = 1; i < activeCaregiverCenters.size(); i++) {
            if (random.nextBoolean()) {
                CaregiverCenter caregiverCenter = activeCaregiverCenters.get(i);
                createDummyServiceRequestForCaregiver(caregiverCenter, serviceRequest, requestedDate, serviceStartTime, serviceEndTime,
                        87.0 + (random.nextDouble() * 38.0));
            }
        }
    }


    public void createDummyServiceRequestForCaregiver(CaregiverCenter caregiverCenter,
                                                      ServiceRequest serviceRequest,
                                                      LocalDate requestedDate,
                                                      LocalTime startTime,
                                                      LocalTime endTime,
                                                      double distanceLog) {

        UUID caregiverId = caregiverCenter.getCaregiver().getCaregiverId();

        // ServiceMatch 생성
        CreateServiceMatchRequest matchRequest = new CreateServiceMatchRequest(
                serviceRequest.getServiceRequestId(),
                caregiverId,
                startTime,
                endTime,
                requestedDate
        );
        UUID serviceMatchId = serviceMatchCommandService.createServiceMatch(matchRequest);

        // ServiceMatch 상태 업데이트
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);
        MatchStatus matchStatus = requestedDate.isAfter(LocalDate.now()) ? MatchStatus.CONFIRMED : MatchStatus.COMPLETED;
        serviceMatch.changeMatchStatus(matchStatus);

        // 리뷰 생성 (COMPLETED인 경우만)
        if (matchStatus == MatchStatus.COMPLETED) {
            Review review = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .serviceMatch(serviceMatch)
                    .reviewScore(4.5 + (0.5 * random.nextDouble()))
                    .reviewContent("더미 리뷰 내용입니다.")
                    .build();
            reviewRepository.save(review);
        }

        // ServiceRequest 상태 동기화
        serviceRequest.changeRequestStatus(ServiceRequestStatus.ASSIGNED);

        // 정산 생성
        CreateSettlementRequest settlementRequest = new CreateSettlementRequest(
                caregiverCenter.getCaregiverCenterId(),
                serviceMatchId,
                distanceLog
        );
        settlementCommandService.createSettlement(settlementRequest);
    }

    public void createDummyServiceRequestForConsumer(Consumer consumer) {
        // 전체 ACTIVE 요양보호사 조회
        List<Caregiver> activeCaregivers = caregiverCenterRepository.findByStatus(CaregiverStatus.ACTIVE)
                .stream()
                .map(CaregiverCenter::getCaregiver)
                .distinct()
                .limit(10) // 앞 10명만
                .toList();

        if (activeCaregivers.isEmpty()) return;

        Caregiver firstCaregiver = activeCaregivers.get(0); // 맨 첫 번째 caregiver

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

                Caregiver caregiver = activeCaregivers.get(random.nextInt(activeCaregivers.size()));

                if (serviceMatchQueryRepository.existsByCaregiverAndDateTime(
                        caregiver.getCaregiverId(), date, startTime, endTime)) {
                    continue;
                }

                createServiceRequestAndMatch(
                        consumer,
                        caregiver,
                        date,
                        startTime,
                        endTime,
                        "테스트용 서비스 요청",
                        false
                );
            }
        }

        // ✅ 9월 5일, DUMMY 주소 + 반복 예약 포함한 특별 케이스
        LocalTime preferredStartTime = LocalTime.of(9, 0);
        LocalTime preferredEndTime = LocalTime.of(12, 0);

        createServiceRequestAndMatch(
                consumer,
                firstCaregiver,
                LocalDate.of(2025, 9, 5),
                preferredStartTime,
                preferredEndTime,
                "주차 공간 협소, 반려견 있음, 치매, 와상 환자이니 전문가가 필요합니다.",
                DUMMY_ADDRESSES[0],
                new Location(DUMMY_LATITUDES[0], DUMMY_LONGITUDES[0]),
                ServiceType.IN_HOME_SUPPORT,
                false
        );

        // ✅ 지난달 1일 케이스
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        createServiceRequestAndMatch(
                consumer,
                firstCaregiver,
                firstDayOfLastMonth,
                preferredStartTime,
                preferredEndTime,
                "지난달 1일 더미 서비스 요청",
                true
        );

        // ✅ 저번 주 월요일 케이스
        LocalDate lastWeekMonday = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        createServiceRequestAndMatch(
                consumer,
                firstCaregiver,
                lastWeekMonday,
                preferredStartTime,
                preferredEndTime,
                "저번 주 월요일 더미 서비스 요청",
                true
        );


        UUID recurringOfferId = dummyRecurringOfferService.createDummyRecurringOfferForConsumer(consumer, false);
        recurringOfferCommandService.approveRecurringStatus(recurringOfferId);
    }

    private void createServiceRequestAndMatch(
            Consumer consumer,
            Caregiver caregiver,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String additionalInfo,
            boolean settlementPaid
    ) {
        createServiceRequestAndMatch(consumer, caregiver, date, startTime, endTime,
                additionalInfo, consumer.getResidentialAddress(), new Location(34.9485, 127.4942), ServiceType.values()[0], settlementPaid);
    }

    private void createServiceRequestAndMatch(
            Consumer consumer,
            Caregiver caregiver,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String additionalInfo,
            String serviceAddress,
            Location location,
            ServiceType serviceType,
            boolean settlementPaid
    ) {
        ServiceRequest serviceRequest = ServiceRequest.builder()
                .consumer(consumer)
                .serviceAddress(serviceAddress)
                .addressType(AddressType.ROAD)
                .location(location)
                .requestDate(date)
                .preferredStartTime(startTime)
                .preferredEndTime(endTime)
                .duration((int) Duration.between(startTime, endTime).toHours())
                .serviceType(serviceType)
                .additionalInformation(additionalInfo)
                .build();

        UUID serviceRequestId = UUID.randomUUID();
        serviceRequest.initializeServiceRequest(serviceRequestId);
        serviceRequestRepository.save(serviceRequest);

        CreateServiceMatchRequest matchRequest = new CreateServiceMatchRequest(
                serviceRequestId,
                caregiver.getCaregiverId(),
                startTime,
                endTime,
                date
        );
        UUID serviceMatchId = serviceMatchCommandService.createServiceMatch(matchRequest);
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);

        if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
            serviceMatch.changeMatchStatus(MatchStatus.CONFIRMED);
        } else {
            serviceMatch.changeMatchStatus(MatchStatus.COMPLETED);

            if (random.nextBoolean()) {
                Review review = Review.builder()
                        .reviewId(UUID.randomUUID())
                        .serviceMatch(serviceMatch)
                        .reviewScore(5.0)
                        .reviewContent("테스트용 리뷰")
                        .build();
                reviewRepository.save(review);
            }
        }

        serviceRequest.changeRequestStatus(ServiceRequestStatus.ASSIGNED);

        CreateSettlementRequest settlementRequest = new CreateSettlementRequest(
                caregiverCenterRepository.findByCaregiver_CaregiverId(caregiver.getCaregiverId()).get(0).getCaregiverCenterId(),
                serviceMatchId,
                100.0
        );
        UUID settlementId = settlementCommandService.createSettlement(settlementRequest);

        // settlementPaid 가 true일 때만 정산 상태 변경
        if (settlementPaid) {
            Settlement settlement = settlementQueryService.getSettlement(settlementId);
            settlement.changePaidStatus();
        }
    }
}
