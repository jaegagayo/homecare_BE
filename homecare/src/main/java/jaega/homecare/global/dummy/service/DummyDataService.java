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
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
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
    private final SettlementQueryService settlementQueryService;
    private final Random random = new Random();

    private final DummyUserService dummyUserService;
    private final DummyCenterService dummyCenterService;
    private final DummyCaregiverService dummyCaregiverService;
    private final DummyConsumerService dummyConsumerService;

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        dummyUserService.generateDummyUsers();

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 생성
        dummyCenterService.generateDummyCenter();

        // 3. 더미 요양보호사 생성
        dummyCaregiverService.generateDummyCaregiver();

        // 4. Consumer 생성
        dummyConsumerService.generateDummyConsumer();

        // 4-1. dummy1@user.com 전용 Consumer 보장 생성
        User dummyUser = userRepository.findByEmail("user1@dummy.com");
        Consumer dummyConsumer = consumerRepository.findByUser(dummyUser);

        // 전용 ServiceRequest / ServiceMatch / VoucherUsage / Review 생성
        createDummyServiceRequestForConsumer(dummyConsumer);

        // 5. ServiceRequest 생성 (Consumer 기반)
        IntStream.range(0, 30).forEach(this::createDummyServiceRequest);
    }

    private UUID createDummyRecurringOfferForConsumer(Consumer consumer, boolean useFirstCaregiver) {
        List<Caregiver> approvedCaregivers = caregiverRepository.findAll();

        if (approvedCaregivers.isEmpty()) return null;

        Caregiver caregiver;
        if (useFirstCaregiver) {
            caregiver = approvedCaregivers.get(0); // ✅ 첫 번째 Caregiver 확정
        } else {
            caregiver = approvedCaregivers.get(random.nextInt(approvedCaregivers.size())); // 랜덤
        }

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
        List<CaregiverCenter> activeCaregiverCenters = caregiverCenterRepository.findByStatus(CaregiverStatus.ACTIVE);

        if (activeCaregiverCenters.isEmpty()) return;

        // --- 1️⃣ 첫 번째 요양보호사 확정 생성 ---
        createDummyForCaregiver(activeCaregiverCenters.get(0), serviceRequest, requestedDate, serviceStartTime, serviceEndTime,
                87.0 + (random.nextDouble() * 38.0));

        // --- 2️⃣ 나머지 요양보호사는 랜덤 생성 ---
        for (int i = 1; i < activeCaregiverCenters.size(); i++) {
            if (random.nextBoolean()) {
                CaregiverCenter caregiverCenter = activeCaregiverCenters.get(i);
                createDummyForCaregiver(caregiverCenter, serviceRequest, requestedDate, serviceStartTime, serviceEndTime,
                        87.0 + (random.nextDouble() * 38.0));
            }
        }
    }

    private void createDummyForCaregiver(CaregiverCenter caregiverCenter,
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

    private void createDummyServiceRequestForConsumer(Consumer consumer) {
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


        UUID recurringOfferId = createDummyRecurringOfferForConsumer(consumer, false);
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

    private static final String[] DUMMY_ADDRESSES = {
            "전라남도 순천시 성동3길 8", "전라남도 순천시 성동3길 2", "전라남도 순천시 조례동 12",
            "전라남도 순천시 연향동 1346", "전라남도 순천시 조례동 1800", "전라남도 순천시 왕지동 23-1",
            "전라남도 순천시 조례동 1659", "전라남도 순천시 봉화1길 146", "전라남도 순천시 성동2길 42",
            "전라남도 순천시 성동2길 46", "전라남도 순천시 조례동 550", "전라남도 순천시 풍덕동 807-7",
            "전라남도 순천시 풍덕동 255-3", "전라남도 순천시 연향동 1659", "전라남도 순천시 매곡동 12-1",

            "전라남도 순천시 매곡동 407", "전남 순천시 석현동 7", "전남 순천시 매곡동 407",
            "전남 순천시 가곡동 429", "전남 순천시 석현동 235", "전남 순천시 조례동 1820",
            "전남 순천시 조례동 68-19", "전남 순천시 조례동 1804", "전남 순천시 조례동 1802",
            "전남 순천시 조례동 1885", "전남 순천시 조례동 550", "전남 순천시 조례동 840",
            "전남 순천시 조례동 1519-12", "전남 순천시 조례동 1450", "전남 순천시 조례동 1421",

            "전남 순천시 조례동 1882", "전남 순천시 조례동 1348", "전남 순천시 조례동 1299-5",
            "전남 순천시 생목동 59", "전남 순천시 조곡동 68-61", "전남 순천시 오천동 951",
            "전남 순천시 오천동 953-1", "전남 순천시 오천동 982", "전남 순천시 연향동 1378",
            "전남 순천시 연향동 1384-3", "전남 순천시 연향동 1386", "전남 순천시 연향동 1387",
            "전남 순천시 연향동 1515", "전남 순천시 해룡면 상삼리 696", "전남 순천시 해룡면 상삼리 691"
    };

    private static final double[] DUMMY_LATITUDES = {
            34.9485, 34.9492, 34.9501, 34.9510, 34.9518, 34.9523, 34.9531, 34.9539, 34.9544, 34.9550,
            34.9556, 34.9561, 34.9567, 34.9572, 34.9578, 34.9583, 34.9589, 34.9594, 34.9600, 34.9605,
            34.9611, 34.9616, 34.9622, 34.9627, 34.9633, 34.9638, 34.9644, 34.9649, 34.9655, 34.9660,
            34.9666, 34.9671, 34.9677, 34.9682, 34.9688, 34.9693, 34.9699, 34.9704, 34.9710, 34.9715,
            34.9721, 34.9726, 34.9732
    };

    private static final double[] DUMMY_LONGITUDES = {
            127.4662, 127.4670, 127.4675, 127.4682, 127.4687, 127.4693, 127.4698, 127.4704, 127.4709, 127.4715,
            127.4720, 127.4726, 127.4731, 127.4737, 127.4742, 127.4748, 127.4753, 127.4759, 127.4764, 127.4770,
            127.4775, 127.4781, 127.4786, 127.4792, 127.4797, 127.4803, 127.4808, 127.4814, 127.4819, 127.4825,
            127.4830, 127.4836, 127.4841, 127.4847, 127.4852, 127.4858, 127.4863, 127.4869, 127.4874, 127.4880,
            127.4885, 127.4891, 127.4896
    };
}
