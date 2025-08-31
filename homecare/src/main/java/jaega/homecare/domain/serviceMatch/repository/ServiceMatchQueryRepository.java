package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.entity.QCaregiverPreference;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.serviceMatch.dto.res.*;

import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.recurringOffer.entity.QRecurringOffer;
import jaega.homecare.domain.review.entity.QReview;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.settlement.dto.res.WorkPlaceDistribution;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static jaega.homecare.domain.serviceMatch.entity.QServiceMatch.serviceMatch;
import static jaega.homecare.domain.serviceRequest.entity.QServiceRequest.serviceRequest;

@Repository
@RequiredArgsConstructor
public class ServiceMatchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CaregiverQueryRepository caregiverQueryRepository;

    /**
     *
     * Center
     *
     */

    // Center의 배정된 내역 조회
    public List<GetServiceMatchByCenterResponse> findMatchesByCenterId(UUID centerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QConsumer consumer = QConsumer.consumer;
        QUser caregiverUser = new QUser("caregiverUser");
        QUser consumerUser = new QUser("consumerUser");

        return queryFactory
                .select(Projections.constructor(
                        GetServiceMatchByCenterResponse.class,
                        consumerUser.name,
                        caregiverUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceType.stringValue(),
                        serviceMatch.matchStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .join(consumer.user, consumerUser)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .orderBy(serviceMatch.serviceDate.desc())
                .fetch();
    }

    public List<GetCaregiverMatchesResponse> findByCaregiverId(UUID caregiverId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QConsumer consumer = QConsumer.consumer;

        QUser caregiverUser = new QUser("caregiverUser");
        QUser consumerUser = new QUser("consumerUser");

        // 1. 기본 정보 조회 (serviceTypes는 비워둠)
        List<GetCaregiverMatchesResponse> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.caregiverId,
                        caregiverUser.name,
                        consumerUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        Expressions.constant(Collections.emptySet()), // ServiceType, 이후 별도 로딩
                        serviceRequest.serviceAddress,
                        Expressions.constant(12000), // TODO: 하드코딩, 시급(추후 제거 필요)
                        serviceMatch.matchStatus,
                        Expressions.nullExpression(String.class) // notes, 추가 내용
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .join(consumer.user, consumerUser)
                .where(caregiver.caregiverId.eq(caregiverId))
                .orderBy(serviceMatch.id.desc())
                .fetch();

        // 2. caregiverIds 추출
        Set<UUID> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesResponse::caregiverId)
                .collect(Collectors.toSet());

        if (caregiverIds.isEmpty()) {
            return baseList; // 결과 없으면 바로 반환
        }

        // 3. serviceTypes 조회 (QueryDSL)
        List<Tuple> rows = caregiverQueryRepository.findServiceTypesByCaregiverIds(caregiverIds);

        // 4. Map<UUID, Set<ServiceType>> 변환
        Map<UUID, Set<ServiceType>> serviceTypeMap = rows.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> tuple.get(1, ServiceType.class), Collectors.toSet())
                ));

        // 5. serviceTypes 채워서 새 DTO 생성
        return baseList.stream()
                .map(base -> new GetCaregiverMatchesResponse(
                        base.serviceMatchId(),
                        base.caregiverId(),
                        base.caregiverName(),
                        base.consumerName(),
                        base.serviceDate(),
                        base.serviceStartTime(),
                        base.serviceEndTime(),
                        serviceTypeMap.getOrDefault(base.caregiverId(), Collections.emptySet()),
                        base.serviceAddress(),
                        base.hourlyWage(),
                        base.status(),
                        base.notes()
                ))
                .toList();
    }

    public DashboardStats getDashboardStatus(UUID centerId, LocalDate date) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> result = queryFactory
                .select(
                        caregiver.countDistinct(), // 총 요양보호사 수
                        ExpressionUtils.as( // 오늘 배정된 요양보호사 수
                                JPAExpressions
                                        .select(serviceMatch.caregiver.countDistinct())
                                        .from(serviceMatch)
                                        .join(serviceMatch.caregiver, caregiver)
                                        .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                                        .where(
                                                caregiverCenter.center.centerId.eq(centerId)
                                                        .and(serviceMatch.serviceDate.eq(date))
                                                        .and(serviceMatch.matchStatus.in(MatchStatus.CONFIRMED))
                                        ),
                                "assignedCaregivers"
                        ),
                        ExpressionUtils.as( // 오늘 이후 배정 대기 중인 요양보호사 수 (PENDING 상태)
                                JPAExpressions
                                        .select(serviceMatch.caregiver.countDistinct())
                                        .from(serviceMatch)
                                        .join(serviceMatch.caregiver, caregiver)
                                        .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                                        .where(
                                                caregiverCenter.center.centerId.eq(centerId)
                                                        .and(serviceMatch.matchStatus.eq(MatchStatus.PENDING))
                                                        .and(serviceMatch.serviceDate.goe(date))
                                        ),
                                "waitingApplicants"
                        ),
                        ExpressionUtils.as( // 오늘 이후 매칭이 없는 미배정 요양보호사 수
                                JPAExpressions
                                        .select(caregiver.countDistinct())
                                        .from(caregiver)
                                        .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                                        .where(
                                                caregiverCenter.center.centerId.eq(centerId)
                                                        .and(caregiverCenter.status.eq(CaregiverStatus.ACTIVE))
                                                        .and(
                                                                JPAExpressions
                                                                        .selectOne()
                                                                        .from(serviceMatch)
                                                                        .where(
                                                                                serviceMatch.caregiver.eq(caregiver)
                                                                                        .and(serviceMatch.serviceDate.goe(date)) // Corrected: Use goe for "on or after today"
                                                                                        .and(serviceMatch.matchStatus.in(MatchStatus.PENDING, MatchStatus.CONFIRMED))
                                                                        )
                                                                        .notExists()
                                                        )
                                        ),
                                "unassignedCaregivers"
                        )
                )
                .from(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId),
                        caregiverCenter.status.eq(CaregiverStatus.ACTIVE)
                )
                .fetch();

        Tuple row = result.get(0);
        return new DashboardStats(
                row.get(0, Long.class), // total caregivers
                row.get(1, Long.class), // assignedCaregivers
                row.get(2, Long.class), // waitingApplicants
                row.get(3, Long.class)  // unassignedCaregivers
        );
    }

    // 요양 보호사 대시보드의 근무지 별 분포 통계 조회
    public List<WorkPlaceDistribution> getWorkPlaceDistributionByServiceType(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QCaregiverPreference preference = QCaregiverPreference.caregiverPreference;

        // 센터 소속 활동 중인 요양보호사 + Preference 조회
        List<Tuple> results = queryFactory
                .select(caregiver, preference)
                .from(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .leftJoin(preference).on(preference.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId),
                        caregiverCenter.status.eq(CaregiverStatus.ACTIVE)
                )
                .fetch();

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        Map<ServiceType, Long> serviceTypeCount = new HashMap<>();

        for (Tuple tuple : results) {
            Caregiver cg = tuple.get(caregiver);
            CaregiverPreference pref = tuple.get(preference);

            // Caregiver + Preference 서비스 타입 합집합
            Set<ServiceType> combined = new HashSet<>();
            if (pref.getServiceTypes() != null) combined.addAll(pref.getServiceTypes());
            if (pref != null && pref.getServiceTypes() != null) combined.addAll(pref.getServiceTypes());

            // 카운트 증가
            for (ServiceType st : combined) {
                serviceTypeCount.put(st, serviceTypeCount.getOrDefault(st, 0L) + 1);
            }
        }

        // 총합 계산
        long total = serviceTypeCount.values().stream().mapToLong(Long::longValue).sum();

        // DTO 변환
        return serviceTypeCount.entrySet().stream()
                .map(entry -> {
                    double percent = total > 0 ? (double) entry.getValue() * 100 / total : 0.0;
                    percent = Math.round(percent * 10) / 10.0; // 소수점 첫째 자리까지
                    return new WorkPlaceDistribution(
                            entry.getKey(),
                            entry.getValue(),
                            percent
                    );
                })
                .toList();
    }

    // 특정 년도, 월(필수), 일(선택) 의 요양보호사 매칭 스케줄 조회
    public List<GetCaregiverMatchesByMonth> findMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate startDate;
        LocalDate endDate;

        if (day != null) {
            startDate = LocalDate.of(year, month, day);
            endDate = startDate;
        } else {
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }

        // 1. 기본 정보 조회 (serviceTypes 제외)
        List<GetCaregiverMatchesByMonth> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesByMonth.class,
                        serviceMatch.serviceMatchId,
                        caregiver.caregiverId,
                        caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        Expressions.constant(Collections.emptySet()),
                        caregiver.address,
                        serviceMatch.matchStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId)
                        .and(serviceMatch.serviceDate.between(startDate, endDate)))
                .orderBy(serviceMatch.serviceDate.desc(), serviceMatch.serviceStartTime.asc())
                .fetch();

        // 2. caregiverId 추출
        Set<UUID> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesByMonth::caregiverId)
                .collect(Collectors.toSet());

        if (caregiverIds.isEmpty()) {
            return baseList;
        }

        // 3. serviceTypes 조회 (QueryDSL)
        List<Tuple> rows = caregiverQueryRepository.findServiceTypesByCaregiverIds(caregiverIds);

        // 4. Map<UUID, Set<ServiceType>> 변환
        Map<UUID, Set<ServiceType>> serviceTypeMap = rows.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> tuple.get(1, ServiceType.class), Collectors.toSet())
                ));

        // 5. DTO 재생성
        return baseList.stream()
                .map(base -> new GetCaregiverMatchesByMonth(
                        base.serviceMatchId(),
                        base.caregiverId(),
                        base.caregiverName(),
                        base.serviceDate(),
                        base.serviceStartTime(),
                        base.serviceEndTime(),
                        serviceTypeMap.getOrDefault(base.caregiverId(), Collections.emptySet()),
                        base.serviceAddress(),
                        base.matchStatus()
                ))
                .toList();
    }

    /**
     * 추천 정기 제안용 ServiceMatch 조회
     *
     * - RecurringOffer 정기 제안 추천을 위해 사용
     *
     * 조회 조건:
     * - 리뷰 점수 4점 이상
     * - 매칭 상태가 CONFIRMED
     * - 아직 정기 제안이 신청되지 않은 일정
     * - 특정 소비자 기준 필터 적용
     */
    public List<ServiceMatch> findRecommendedServiceMatches(UUID consumerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QReview review = QReview.review;
        QRecurringOffer recurringOffer = QRecurringOffer.recurringOffer;

        return queryFactory
                .select(serviceMatch)
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest)
                .join(serviceMatch.serviceRequest.consumer)
                .leftJoin(review).on(review.serviceMatch.eq(serviceMatch))
                .leftJoin(recurringOffer)
                .on(recurringOffer.consumer.eq(serviceMatch.serviceRequest.consumer)
                        .and(recurringOffer.caregiver.eq(serviceMatch.caregiver)))
                .where(
                        serviceMatch.serviceRequest.consumer.consumerId.eq(consumerId), // 특정 소비자
                        serviceMatch.matchStatus.eq(MatchStatus.COMPLETED),             // 매칭 완료 상태
                        review.reviewScore.goe(4.0),                                     // 리뷰 점수 4 이상
                        recurringOffer.id.isNull()                                        // 아직 정기 제안 없음
                )
                .fetch();
    }


    /**
     *
     * Consumer
     *
     */

    // Consumer의 주간 일정 조회 (월 ~ 일)
    public List<ConsumerScheduleResponse> findConsumerWeeklySchedule(UUID consumerId, LocalDate weekStart, LocalDate weekEnd) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(Projections.constructor(
                        ConsumerScheduleResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus,
                        serviceRequest.requestStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .where(
                        consumer.consumerId.eq(consumerId),
                        serviceMatch.serviceDate.between(weekStart, weekEnd),
                        serviceMatch.matchStatus.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .orderBy(serviceMatch.serviceDate.asc(), serviceMatch.serviceStartTime.asc())
                .fetch();

    }

    // Consumer의 일정 상세 조회
    public ConsumerScheduleDetailResponse findConsumerScheduleDetail(UUID serviceMatchId) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser caregiverUser = caregiver.user;
        QReview review = QReview.review;

        return queryFactory.
                select(Projections.constructor(
                        ConsumerScheduleDetailResponse.class,
                        caregiverUser.name,
                        caregiverUser.phone,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.duration,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus,
                        review.id.isNull()
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceMatch.caregiver, caregiver)
                .leftJoin(review).on(review.serviceMatch.eq(serviceMatch))
                .where(serviceMatch.serviceMatchId.eq(serviceMatchId))
                .fetchOne();
    }

    // Consumer의 가장 가까운 확정 일정 조회
    public ConsumerNextScheduleResponse findConsumerNextSchedule(UUID consumerId) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser caregiverUser = caregiver.user;

        return queryFactory.
                select(Projections.constructor(
                        ConsumerNextScheduleResponse.class,
                        caregiverUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .join(serviceMatch.caregiver, caregiver)
                .where(
                        consumer.consumerId.eq(consumerId),
                        serviceMatch.matchStatus.eq(MatchStatus.CONFIRMED),
                        serviceMatch.serviceDate.after(LocalDate.now())
                                .or(
                                        serviceMatch.serviceDate.eq(LocalDate.now())
                                                .and(serviceMatch.serviceStartTime.goe(LocalTime.now()))
                                )
                )
                .orderBy(serviceMatch.serviceDate.asc(), serviceMatch.serviceStartTime.asc())
                .limit(1)
                .fetchOne();

    }

    // 완료된 일정 중 리뷰가 없는 일정 조회
    public List<GetScheduleWithoutReviewResponse> findCompletedScheduleWithoutReview(UUID consumerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QReview review = QReview.review;

        return queryFactory
                .select(Projections.constructor(
                        GetScheduleWithoutReviewResponse.class,
                        serviceMatch.serviceMatchId,
                        serviceMatch.caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceType
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceMatch.caregiver, caregiver)
                .leftJoin(review).on(review.serviceMatch.eq(serviceMatch))
                .where(
                        serviceRequest.consumer.consumerId.eq(consumerId),
                        serviceMatch.matchStatus.eq(MatchStatus.COMPLETED),
                        review.id.isNull()
                )
                .orderBy(serviceMatch.serviceDate.desc(), serviceMatch.serviceStartTime.desc())
                .fetch();

    }


    /**
     *
     * Caregiver
     *
     */

    // Caregiver의 주간 일정 조회 (월 ~ 일)
    public List<CaregiverScheduleResponse> findCaregiverWeeklySchedule(UUID caregiverId, LocalDate weekStart, LocalDate weekEnd) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(Projections.constructor(
                        CaregiverScheduleResponse.class,
                        serviceMatch.serviceMatchId,
                        consumer.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus,
                        serviceRequest.requestStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .where(
                        caregiver.caregiverId.eq(caregiverId),
                        serviceMatch.serviceDate.between(weekStart, weekEnd),
                        serviceMatch.matchStatus.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .orderBy(serviceMatch.serviceDate.asc(), serviceMatch.serviceStartTime.asc())
                .fetch();
    }

    // Caregiver의 일정 상세 조회
    public CaregiverScheduleDetailResponse findCaregiverScheduleDetail(UUID serviceMatchId) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QUser consumerUser = consumer.user;

        return queryFactory.
                select(Projections.constructor(
                        CaregiverScheduleDetailResponse.class,
                        consumerUser.name,
                        consumerUser.phone,
                        consumer.guardianName,
                        consumer.guardianPhone,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.duration,
                        consumer.careGrade,
                        consumer.disease,
                        consumer.weight,
                        consumer.cognitiveStatus,
                        consumer.livingSituation,
                        serviceRequest.serviceAddress,
                        consumer.entranceType,
                        serviceRequest.additionalInformation,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .where(serviceMatch.serviceMatchId.eq(serviceMatchId))
                .fetchOne();
    }

    // 특정 날짜의 Caregiver 일정 조회
    public List<CaregiverScheduleResponse> findCaregiverScheduleByDate(UUID caregiverId, LocalDate date) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(Projections.constructor(
                        CaregiverScheduleResponse.class,
                        serviceMatch.serviceMatchId,
                        consumer.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus,
                        serviceRequest.requestStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .where(
                        caregiver.caregiverId.eq(caregiverId),
                        serviceMatch.serviceDate.eq(date),
                        serviceMatch.matchStatus.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED)
                )
                .orderBy(serviceMatch.serviceStartTime.asc())
                .fetch();
    }

    public List<ConsumerCancelledScheduleResponse> getCancelledSchedules(UUID consumerId) {
        return queryFactory
                .select(Projections.constructor(
                        ConsumerCancelledScheduleResponse.class,
                        serviceMatch.serviceMatchId,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceMatch.caregiver.user.name
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .where(serviceRequest.consumer.consumerId.eq(consumerId)
                        .and(serviceMatch.matchStatus.eq(MatchStatus.CANCELLED)))
                .orderBy(serviceMatch.serviceDate.asc(), serviceMatch.serviceStartTime.asc())
                .fetch();
    }

    /**
     * 특정 요양보호사에 대해 주어진 날짜, 시간 범위에 중복된 매칭이 존재하는지 확인
     */
    public boolean existsByCaregiverAndDateTime(UUID caregiverId, LocalDate serviceDate, LocalTime startTime, LocalTime endTime) {
        Long count = queryFactory
                .select(serviceMatch.count())
                .from(serviceMatch)
                .where(
                        serviceMatch.caregiver.caregiverId.eq(caregiverId),
                        serviceMatch.serviceDate.eq(serviceDate),
                        serviceMatch.matchStatus.in(MatchStatus.CONFIRMED, MatchStatus.COMPLETED),
                        serviceMatch.serviceStartTime.lt(endTime)
                                .and(serviceMatch.serviceEndTime.gt(startTime)) // 시간 겹침 체크
                )
                .fetchOne();

        return count != null && count > 0;
    }
}