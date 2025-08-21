package jaega.homecare.domain.workLog.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.workLog.dto.res.*;
import jaega.homecare.domain.workLog.entity.QWorkLog;
import jaega.homecare.domain.workLog.entity.WorkLog;
import jaega.homecare.domain.workLog.entity.WorkStatus;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
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
public class WorkLogQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final CaregiverRepository caregiverRepository;

    // 정산 상태 기반 정산 내역 조회
    public List<GetWorkLogByPaid> findWorkLogByPaid(UUID centerId, Boolean isPaid) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetWorkLogByPaid.class,
                        workLog.workLogId,
                        workLog.workDate,
                        caregiver.user.name
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(isPaid)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(
                        workLog.workDate.desc())
                .fetch();
    }

    // 센터에 등록된 요양보호사 정산 내역 조회 (일별)
    public List<GetWorkLogByDateResponse> findWorkLogByDate(UUID centerId, LocalDate date) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetWorkLogByDateResponse.class,
                        workLog.workLogId,
                        workLog.workDate,
                        workLog.workStartTime,
                        workLog.workEndTime,
                        caregiver.user.name
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.workDate.eq(date)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(workLog.workStartTime.asc())
                .fetch();
    }

    // 센터에 등록된 요양보호사 정산 내역 조회 (월별)
    public List<GetCaregiverMatchesByMonth> findWorkLogByMonth(UUID centerId, int year, int month, Integer day) {
        QWorkLog workLog = QWorkLog.workLog;
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
                        workLog.workLogId,
                        caregiver.caregiverId,
                        user.name,
                        workLog.workDate,
                        workLog.workStartTime,
                        workLog.workEndTime,
                        Expressions.constant(Collections.emptySet()),
                        caregiver.address,
                        workLog.status
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        workLog.workDate.between(startDate, endDate),
                        caregiverCenter.center.centerId.eq(centerId)
                )
                .orderBy(workLog.workDate.desc(), workLog.createdAt.desc())
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
                        base.workLogId(),
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
    public List<WorkLog> findOverlappingWorkLog(
            Caregiver caregiver,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        QWorkLog workLog = QWorkLog.workLog;

        return queryFactory
                .selectFrom(workLog)
                .where(
                        workLog.caregiver.eq(caregiver),
                        workLog.workDate.eq(date),
                        workLog.status.eq(WorkStatus.PLANNED),
                        workLog.workStartTime.lt(endTime),
                        workLog.workEndTime.gt(startTime)
                )
                .fetch();
    }

    /**
     * 대시보드
     */

    // 오늘 근무하는 요양보호사 수 조회
    public Long countCaregiversWorkingToday(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(workLog.caregiver.countDistinct())
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
                                .and(workLog.workDate.eq(today))
                                .and(workLog.status.in(WorkStatus.PLANNED, WorkStatus.COMPLETED))
                )
                .fetchOne();
    }

    // 오늚 미배정된 요양보호사 수 조회
    public Long countUnassignedCaregiversToday(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate today = LocalDate.now();

        // 오늘 배정된 caregiverId 서브쿼리
        List<UUID> assignedCaregiverIds = queryFactory
                .select(workLog.caregiver.caregiverId)
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.workDate.eq(today))
                .fetch();

        // 전체 센터 소속 caregiver 중 오늘 미배정인 수
        return queryFactory
                .select(caregiver.count())
                .from(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
                                .and(caregiver.caregiverId.notIn(assignedCaregiverIds))
                )
                .fetchOne();
    }

    // 배정 대기인 요양보호사 수 조회
    public Long countWaitingApplicants(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(workLog.count())
                .from(workLog)
                .leftJoin(workLog.caregiver, caregiver)
                .leftJoin(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        workLog.status.eq(WorkStatus.PLANNED)
                                .and(workLog.workDate.eq(today))
                                .and(
                                        workLog.caregiver.isNull()
                                                .or(caregiverCenter.center.centerId.eq(centerId))
                                )
                )
                .fetchOne();
    }

    // 요양 보호사 대시보드의 근무지 별 분포 통계 조회
    public List<WorkPlaceDistribution> getWorkPlaceDistributionByServiceType(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        // center 소속 활성 보호사 + serviceTypes 조회
        List<Tuple> rows = queryFactory
                .select(caregiver, caregiver.serviceTypes)
                .from(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId),
                        caregiverCenter.status.eq(CaregiverStatus.ACTIVE)
                )
                .fetch();

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        // ServiceType별 카운트 계산
        Map<ServiceType, Long> serviceTypeCount = new HashMap<>();
        for (Tuple row : rows) {
            Set<ServiceType> serviceTypes = row.get(caregiver.serviceTypes);
            if (serviceTypes != null) {
                for (ServiceType st : serviceTypes) {
                    serviceTypeCount.put(st, serviceTypeCount.getOrDefault(st, 0L) + 1);
                }
            }
        }

        // 총합 계산
        long total = serviceTypeCount.values().stream().mapToLong(Long::longValue).sum();

        // DTO 변환
        return serviceTypeCount.entrySet().stream()
                .map(entry -> new WorkPlaceDistribution(
                        entry.getKey(),
                        entry.getValue(),
                        total > 0 ? (double) entry.getValue() * 100 / total : 0.0
                ))
                .toList();
    }

    /**
     *  정산 페이지
     **/

    // 센터에 등록된 요양보호사 정산 금액, 건수 조회
    public GetSettlementCenterSummaryResponse getSettlementCenterSummary(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> results = queryFactory
                .select(
                        workLog.status,
                        workLog.settlementAmount.sum(),
                        workLog.count()
                )
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .groupBy(workLog.status)
                .fetch();

        BigDecimal totalAmount = BigDecimal.ZERO;
        long completedCount = 0L;
        long plannedCount = 0L;
        long cancelledCount = 0L;

        for (Tuple tuple : results) {
            WorkStatus status = tuple.get(workLog.status);
            BigDecimal sumAmount = tuple.get(workLog.settlementAmount.sum());
            Long count = tuple.get(workLog.count());

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
        QWorkLog workLog = QWorkLog.workLog;

        // 총 정산 금액 (isPaid = true만)
        BigDecimal totalAmount = Optional.ofNullable(
                queryFactory
                        .select(workLog.settlementAmount.sum())
                        .from(workLog)
                        .where(
                                workLog.caregiver.caregiverId.eq(caregiverId),
                                workLog.status.eq(WorkStatus.COMPLETED),
                                workLog.isPaid.eq(true)
                        )
                        .fetchOne()
        ).orElse(BigDecimal.ZERO);

        // 상태별 카운트
        long completedCount = Optional.ofNullable(
                queryFactory
                        .select(workLog.count())
                        .from(workLog)
                        .where(
                                workLog.caregiver.caregiverId.eq(caregiverId),
                                workLog.status.eq(WorkStatus.COMPLETED)
                        )
                        .fetchOne()
        ).orElse(0L);

        long plannedCount = Optional.ofNullable(
                queryFactory
                        .select(workLog.count())
                        .from(workLog)
                        .where(
                                workLog.caregiver.caregiverId.eq(caregiverId),
                                workLog.status.eq(WorkStatus.PLANNED)
                        )
                        .fetchOne()
        ).orElse(0L);

        long cancelledCount = Optional.ofNullable(
                queryFactory
                        .select(workLog.count())
                        .from(workLog)
                        .where(
                                workLog.caregiver.caregiverId.eq(caregiverId),
                                workLog.status.eq(WorkStatus.CANCELLED)
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
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiverCenter.center.centerId.eq(centerId));

        // 상태 필터
        if (status != null) {
            where.and(workLog.status.eq(status));
        } else {
            where.and(workLog.status.eq(WorkStatus.COMPLETED));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(workLog.workDate.year().eq(year)
                    .and(workLog.workDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        workLog.workDate,
                        workLog.workStartTime,
                        workLog.workEndTime,
                        workLog.settlementAmount,
                        workLog.status
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(where)
                .orderBy(
                        workLog.modifiedAt
                                .coalesce(workLog.createdAt)
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
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiver.caregiverId.eq(caregiverId));

        // 상태 필터
        if (status != null) {
            where.and(workLog.status.eq(status));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(workLog.workDate.year().eq(year)
                    .and(workLog.workDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        workLog.workDate,
                        workLog.workStartTime,
                        workLog.workEndTime,
                        workLog.settlementAmount,
                        workLog.status
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(where)
                .orderBy(
                        workLog.modifiedAt
                                .coalesce(workLog.createdAt)
                                .desc()
                )
                .fetch();
    }

    // 이번 달 총 정산내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId, int monthsBack) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.minusMonths(monthsBack - 1).withDayOfMonth(1);

        List<GetMonthlyPaymentResponse> rawResults = queryFactory
                .select(Projections.constructor(
                        GetMonthlyPaymentResponse.class,
                        workLog.workDate.year(),
                        workLog.workDate.month(),
                        workLog.settlementAmount.sum()
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(true)
                        .and(workLog.status.eq(WorkStatus.COMPLETED))
                        .and(caregiverCenter.center.centerId.eq(centerId))
                        .and(workLog.workDate.goe(startMonth)))
                .groupBy(workLog.workDate.year(), workLog.workDate.month())
                .orderBy(workLog.workDate.year().desc(), workLog.workDate.month().desc())
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
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 최근 7일

        // 실제 DB에서 미정산 내역 조회
        List<GetDailyUnsettledResponse> rawResults = queryFactory
                .select(Projections.constructor(
                        GetDailyUnsettledResponse.class,
                        workLog.workDate,
                        workLog.count(),
                        workLog.settlementAmount.sum()
                ))
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(false)
                        .and(workLog.status.ne(WorkStatus.COMPLETED))
                        .and(caregiverCenter.center.centerId.eq(centerId))
                        .and(workLog.workDate.between(startDate, today))
                )
                .groupBy(workLog.workDate)
                .orderBy(workLog.workDate.asc())
                .fetch();

        // 누락된 날짜 채우기
        Map<LocalDate, GetDailyUnsettledResponse> map = rawResults.stream()
                .collect(Collectors.toMap(GetDailyUnsettledResponse::date, r -> r));

        List<GetDailyUnsettledResponse> filled = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate date = startDate.plusDays(i);
            filled.add(map.getOrDefault(date, new GetDailyUnsettledResponse(date, 0L, BigDecimal.ZERO)));
        }

        return filled;
    }

    // 이번 달 누적 정산 금액 조회
    public BigDecimal getTotalSettledAmountThisMonth(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);

        return queryFactory
                .select(workLog.settlementAmount.sum())
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(true)
                        .and(workLog.workDate.goe(firstDay))
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .fetchOne();
    }

    // 미정산 건수 조회
    public Long countUnsettled(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(workLog.count())
                .from(workLog)
                .join(workLog.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(false)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .fetchOne();
    }
}
