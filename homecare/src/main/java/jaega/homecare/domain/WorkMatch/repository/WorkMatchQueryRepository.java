package jaega.homecare.domain.WorkMatch.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkLog.entity.QWorkLog;
import jaega.homecare.domain.WorkMatch.dto.res.*;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkMatchQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final CaregiverRepository caregiverRepository;

    // 센터에 등록된 요양보호사 정산 내역 조회
    public List<GetCaregiverMatchesByMonth> findWorkMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
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
                        workMatch.workMatchId,
                        caregiver.caregiverId,
                        user.name,
                        workMatch.workDate,
                        workMatch.startTime,
                        workMatch.endTime,
                        Expressions.constant(Collections.emptySet()),
                        caregiver.address,
                        workMatch.status
                ))
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))  // 필터용 join 추가
                .where(
                        workMatch.workDate.between(startDate, endDate),
                        caregiverCenter.center.centerId.eq(centerId)
                )
                .orderBy(workMatch.workDate.desc())
                .fetch();

        // 2. caregiverId 추출
        Set<UUID> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesByMonth::caregiverId)
                .collect(Collectors.toSet());

        if (caregiverIds.isEmpty()) {
            return baseList;
        }

        // 3. serviceTypes 별도 조회
        List<Object[]> rows = caregiverRepository.findServiceTypesByCaregiverIds(caregiverIds);

        // 4. Map<Long, Set<ServiceType>> 변환
        Map<UUID, Set<ServiceType>> serviceTypeMap = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> (UUID) row[0],
                        Collectors.mapping(row -> (ServiceType) row[1], Collectors.toSet())
                ));

        // 5. DTO 재생성
        return baseList.stream()
                .map(base -> new GetCaregiverMatchesByMonth(
                        base.workMatchId(),
                        base.caregiverId(),
                        base.caregiverName(),
                        base.workDate(),
                        base.startTime(),
                        base.endTime(),
                        serviceTypeMap.getOrDefault(base.caregiverId(), Collections.emptySet()),
                        base.address(),
                        base.status()
                ))
                .toList();
    }

    // 매칭 알고리즘 사전 필터링
    public List<WorkMatch> findOverlappingWorkMatches(
            Caregiver caregiver,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        QWorkMatch wm = QWorkMatch.workMatch;

        return queryFactory
                .selectFrom(wm)
                .where(
                        wm.caregiver.eq(caregiver),
                        wm.workDate.eq(date),
                        wm.status.eq(WorkStatus.PLANNED),
                        wm.startTime.lt(endTime),
                        wm.endTime.gt(startTime)
                )
                .fetch();
    }

    /**
     * 대시보드의 통계를 위한 api
     */

    // 오늘 근무하는 요양보호사 수 조회
    public Long countCaregiversWorkingToday(UUID centerId) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(workMatch.caregiver.countDistinct())
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .where(
                        workMatch.workDate.eq(today.plusDays(1))
                                .and(caregiver.center.centerId.eq(centerId))
                                .and(workMatch.status.in(WorkStatus.PLANNED, WorkStatus.COMPLETED))
                )
                .fetchOne();
    }

    // 오늚 미배정된 요양보호사 수 조회
    public Long countUnassignedCaregiversToday(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QWorkMatch workMatch = QWorkMatch.workMatch;

        LocalDate today = LocalDate.now();

        // 서브쿼리: 오늘 배정된 caregiverId
        List<UUID> assignedCaregiverIds = queryFactory
                .select(workMatch.caregiver.caregiverId)
                .from(workMatch)
                .where(workMatch.workDate.eq(today))
                .fetch();

        // 전체 센터 소속 caregiver 중 오늘 미배정인 수
        return queryFactory
                .select(caregiver.count())
                .from(caregiver)
                .where(
                        caregiver.center.centerId.eq(centerId)
                                .and(caregiver.caregiverId.notIn(assignedCaregiverIds))
                )
                .fetchOne();
    }

    // 배정 대기인 요양보호사 수 조회
    public Long countWaitingApplicants(UUID centerId) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(workMatch.count())
                .from(workMatch)
                .leftJoin(workMatch.caregiver, caregiver)
                .where(
                        workMatch.status.eq(WorkStatus.PLANNED)
                                .and(workMatch.caregiver.isNull())
                                .and(workMatch.workDate.eq(LocalDate.now()))
                                .and(workMatch.caregiver.center.centerId.eq(centerId).or(workMatch.caregiver.isNull())) // caregiver 없으면 center 조건 제외 가능
                )
                .fetchOne();
    }

    // 요양 보호사 대시보드의 근무지 별 분포 통계 조회
    public List<WorkPlaceDistribution> getWorkPlaceDistributionByServiceType(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;

        // 1. centerId에 속한 보호사 ID 리스트 가져오기
        List<Long> caregiverIds = queryFactory
                .select(caregiver.id)
                .from(caregiver)
                .where(
                        caregiver.center.centerId.eq(centerId),
                        caregiver.status.eq(CaregiverStatus.ACTIVE)
                )
                .fetch();

        if (caregiverIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. JPQL로 serviceTypes 조회
        List<Object[]> rows = caregiverRepository.findServiceTypesByIds(new HashSet<>(caregiverIds));
        // rows = [ [caregiverId, ServiceType], [caregiverId, ServiceType], ... ]

        // 3. ServiceType별 카운트 계산
        Map<ServiceType, Long> serviceTypeCount = new HashMap<>();
        for (Object[] row : rows) {
            ServiceType st = (ServiceType) row[1];
            serviceTypeCount.put(st, serviceTypeCount.getOrDefault(st, 0L) + 1);
        }

        // 4. 총합 계산
        long total = serviceTypeCount.values().stream().mapToLong(Long::longValue).sum();

        // 5. DTO 변환
        return serviceTypeCount.entrySet().stream()
                .map(entry -> new WorkPlaceDistribution(
                        entry.getKey(),
                        entry.getValue(),
                        total > 0 ? (double) entry.getValue() * 100 / total : 0.0
                ))
                .toList();
    }

    // 정산 페이지

    // 센터에 등록된 요양보호사 정산 금액, 건수 조회
    public GetSettlementCenterSummaryResponse getSettlementCenterSummary(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;

        List<Tuple> results = queryFactory
                .select(
                        workMatch.status,
                        workLog.settlementAmount.sum(),
                        workMatch.count()
                )
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .leftJoin(workLog).on(workLog.workMatch.eq(workMatch))
                .where(caregiver.center.centerId.eq(centerId))
                .groupBy(workMatch.status)
                .fetch();

        BigDecimal totalAmount = BigDecimal.ZERO;
        long completedCount = 0L;
        long plannedCount = 0L;
        long cancelledCount = 0L;

        for (Tuple tuple : results) {
            WorkStatus status = tuple.get(workMatch.status);
            BigDecimal sumAmount = tuple.get(workLog.settlementAmount.sum());
            Long count = tuple.get(workMatch.count());

            if (status == WorkStatus.COMPLETED) {
                completedCount = count;
                totalAmount = totalAmount.add(sumAmount != null ? sumAmount : BigDecimal.ZERO);
            } else if (status == WorkStatus.PLANNED) {
                plannedCount = count;
            } else if (status == WorkStatus.CANCELLED) {
                cancelledCount = count;
            }
        }

        return new GetSettlementCenterSummaryResponse(
                totalAmount,
                completedCount,
                plannedCount,
                cancelledCount
        );
    }

    // 요양보호사 개별 정산 금액, 건수 조회
    public GetCaregiverSettlementSummaryResponse getCaregiverSettlementSummary(UUID caregiverId) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QWorkLog workLog = QWorkLog.workLog;

        // 총 정산 금액 (isPaid = true만)
        BigDecimal totalAmount = Optional.ofNullable(
                queryFactory
                        .select(workLog.settlementAmount.sum())
                        .from(workLog)
                        .join(workLog.workMatch, workMatch)
                        .where(
                                workMatch.caregiver.caregiverId.eq(caregiverId),
                                workMatch.status.eq(WorkStatus.COMPLETED),
                                workLog.isPaid.eq(true)
                        )
                        .fetchOne()
        ).orElse(BigDecimal.ZERO);

        // 상태별 카운트
        long completedCount = Optional.ofNullable(
                queryFactory
                        .select(workMatch.count())
                        .from(workMatch)
                        .where(
                                workMatch.caregiver.caregiverId.eq(caregiverId),
                                workMatch.status.eq(WorkStatus.COMPLETED)
                        )
                        .fetchOne()
        ).orElse(0L);

        long plannedCount = Optional.ofNullable(
                queryFactory
                        .select(workMatch.count())
                        .from(workMatch)
                        .where(
                                workMatch.caregiver.caregiverId.eq(caregiverId),
                                workMatch.status.eq(WorkStatus.PLANNED)
                        )
                        .fetchOne()
        ).orElse(0L);

        long cancelledCount = Optional.ofNullable(
                queryFactory
                        .select(workMatch.count())
                        .from(workMatch)
                        .where(
                                workMatch.caregiver.caregiverId.eq(caregiverId),
                                workMatch.status.eq(WorkStatus.CANCELLED)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new GetCaregiverSettlementSummaryResponse(
                totalAmount,
                completedCount,
                plannedCount,
                cancelledCount
        );
    }

    // 센터에 등록된 요양보호사의 근무 상태, 월-연도별 내역 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCenter(
            UUID centerId,
            WorkStatus status,   // nullable
            Integer year,        // nullable
            Integer month        // nullable
    ) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiver.center.centerId.eq(centerId));

        // 상태 필터
        if (status != null) {
            where.and(workMatch.status.eq(status));
        } else {
            where.and(workMatch.status.eq(WorkStatus.COMPLETED));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(workMatch.workDate.year().eq(year)
                    .and(workMatch.workDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        workMatch.workDate,
                        workLog.workTime_start,
                        workLog.workTime_end,
                        workLog.settlementAmount,
                        workMatch.status
                ))
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .leftJoin(workLog).on(workLog.workMatch.eq(workMatch))
                .where(where)
                .orderBy(
                        workLog.modifiedAt
                                .coalesce(workLog.createdAt)
                                .coalesce(workMatch.modifiedAt)
                                .coalesce(workMatch.createdAt)
                                .desc()
                )
                .fetch();
    }

    // 개별 요양보호사의 근무 상태, 월-연도별 내역 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCaregiver(
            UUID caregiverId,
            WorkStatus status,   // nullable
            Integer year,        // nullable
            Integer month        // nullable
    ) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiver.caregiverId.eq(caregiverId));

        // 상태 필터
        if (status != null) {
            where.and(workMatch.status.eq(status));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(workMatch.workDate.year().eq(year)
                    .and(workMatch.workDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        workMatch.workDate,
                        workLog.workTime_start,
                        workLog.workTime_end,
                        workLog.settlementAmount,
                        workMatch.status
                ))
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .leftJoin(workLog).on(workLog.workMatch.eq(workMatch))
                .where(where)
                .orderBy(
                        workLog.modifiedAt
                                .coalesce(workLog.createdAt)
                                .coalesce(workMatch.modifiedAt)
                                .coalesce(workMatch.createdAt)
                                .desc()
                )
                .fetch();
    }

    // 이번 달 총 정산내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId, int monthsBack) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;

        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.minusMonths(monthsBack - 1).withDayOfMonth(1);

        List<GetMonthlyPaymentResponse> rawResults = queryFactory
                .select(Projections.constructor(
                        GetMonthlyPaymentResponse.class,
                        workMatch.workDate.year(),
                        workMatch.workDate.month(),
                        workLog.settlementAmount.sum()
                ))
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .where(workLog.isPaid.eq(true)
                        .and(workMatch.status.eq(WorkStatus.COMPLETED))
                        .and(caregiver.center.centerId.eq(centerId))
                        .and(workMatch.workDate.goe(startMonth)))
                .groupBy(workMatch.workDate.year(), workMatch.workDate.month())
                .orderBy(workMatch.workDate.year().desc(), workMatch.workDate.month().desc())
                .fetch();

        // 누락된 월 채우기
        Map<String, BigDecimal> map = rawResults.stream()
                .collect(Collectors.toMap(
                        r -> r.year() + "-" + r.month(),
                        GetMonthlyPaymentResponse::totalAmount
                ));

        List<GetMonthlyPaymentResponse> filled = new ArrayList<>();
        for (int i = 0; i < monthsBack; i++) {
            LocalDate target = now.minusMonths(i);
            String key = target.getYear() + "-" + target.getMonthValue();
            filled.add(new GetMonthlyPaymentResponse(
                    target.getYear(),
                    target.getMonthValue(),
                    map.getOrDefault(key, BigDecimal.ZERO)
            ));
        }

        return filled;
    }

    // 일주일 간 미정산 내역 조회
    public List<GetDailyUnsettledResponse> getDailyUnsettledCount(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;

        LocalDate startDate = LocalDate.now().minusDays(6); // 오늘 포함 최근 7일

        return queryFactory
                .select(Projections.constructor(
                        GetDailyUnsettledResponse.class,
                        workMatch.workDate,
                        workLog.count(),
                        workLog.settlementAmount.sum()
                ))
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .where(workLog.isPaid.eq(false)
                        .and(workMatch.status.ne(WorkStatus.COMPLETED))
                        .and(caregiver.center.centerId.eq(centerId))
                        .and(workMatch.workDate.goe(startDate)))
                .groupBy(workMatch.workDate)
                .orderBy(workMatch.workDate.desc())
                .fetch();
    }
}
