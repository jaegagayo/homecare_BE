package jaega.homecare.domain.settlement.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.entity.QSettlement;
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SettlementQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final SettlementRepository settlementRepository;
    private final CaregiverRepository caregiverRepository;

    // 정산 상태 기반 정산 내역 조회
    // TODO : 추후 정산 날짜 속성 추가 시 날짜 속성 리팩터링 필요
    public List<GetSettlementByPaid> findSettlementByPaid(UUID centerId, Boolean isPaid) {
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetSettlementByPaid.class,
                        settlement.settlementId,
                        settlement.serviceMatch.serviceDate,
                        caregiver.user.name
                ))
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .where(settlement.isPaid.eq(isPaid)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(settlement.serviceMatch.serviceDate.desc())
                .fetch();
    }

    // 센터에 등록된 요양보호사 정산 내역 조회 (일별)
    public List<GetSettlementByDateResponse> findSettlementByDate(UUID centerId, LocalDate date) {
        QSettlement settlement = QSettlement.settlement;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetSettlementByDateResponse.class,
                        settlement.settlementId,
                        settlement.serviceMatch.serviceDate,
                        settlement.serviceMatch.serviceStartTime,
                        settlement.serviceMatch.serviceEndTime,
                        caregiverCenter.caregiver.user.name
                ))
                .from(settlement)
                .join(settlement.serviceMatch, serviceMatch)
                .join(caregiverCenter)
                .on(caregiverCenter.caregiver.eq(serviceMatch.caregiver)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .where(settlement.serviceMatch.serviceDate.eq(date)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(settlement.serviceMatch.serviceStartTime.asc())
                .fetch();
    }

    // 매칭 알고리즘 사전 필터링
    public List<Settlement> findOverlappingWorkLog(
            Caregiver caregiver,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        QSettlement settlement = QSettlement.settlement;

        return queryFactory
                .selectFrom(settlement)
                .where(
                        settlement.serviceMatch.caregiver.eq(caregiver),
                        settlement.serviceMatch.serviceDate.eq(date),
                        settlement.serviceMatch.matchStatus.eq(MatchStatus.PENDING),
                        settlement.serviceMatch.serviceStartTime.lt(endTime),
                        settlement.serviceMatch.serviceEndTime.gt(startTime)
                )
                .fetch();
    }

    /**
     *  정산 페이지
     **/

    // 센터에 등록된 요양보호사 정산 금액, 건수 조회
    // TODO : 정산 금액 때문에 현재 클래스에 위치해있으며, 도메인으로 각각 분리하도록 리팩터링 필요
    public GetSettlementCenterSummaryResponse getSettlementCenterSummary(UUID centerId) {
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> results = queryFactory
                .select(
                        settlement.serviceMatch.matchStatus,
                        settlement.settlementAmount.sum(),
                        settlement.count()
                )
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .groupBy(settlement.serviceMatch.matchStatus)
                .fetch();

        BigDecimal totalAmount = BigDecimal.ZERO;
        long completedCount = 0L;
        long plannedCount = 0L;
        long cancelledCount = 0L;

        for (Tuple tuple : results) {
            MatchStatus status = tuple.get(settlement.serviceMatch.matchStatus);
            BigDecimal sumAmount = tuple.get(settlement.settlementAmount.sum());
            Long count = tuple.get(settlement.count());

            if (status == MatchStatus.COMPLETED) {
                completedCount = count;
                totalAmount = totalAmount.add(sumAmount != null ? sumAmount : BigDecimal.ZERO);
            } else if (status == MatchStatus.PENDING) {
                plannedCount = count;
            } else if (status == MatchStatus.CANCELLED) {
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
        QSettlement settlement = QSettlement.settlement;

        // 총 정산 금액 (isPaid = true만)
        BigDecimal totalAmount = Optional.ofNullable(
                queryFactory
                        .select(settlement.settlementAmount.sum())
                        .from(settlement)
                        .where(
                                settlement.serviceMatch.caregiver.caregiverId.eq(caregiverId),
                                settlement.serviceMatch.matchStatus.eq(MatchStatus.COMPLETED),
                                settlement.isPaid.eq(true)
                        )
                        .fetchOne()
        ).orElse(BigDecimal.ZERO);

        // 상태별 카운트
        long completedCount = Optional.ofNullable(
                queryFactory
                        .select(settlement.count())
                        .from(settlement)
                        .where(
                                settlement.serviceMatch.caregiver.caregiverId.eq(caregiverId),
                                settlement.serviceMatch.matchStatus.eq(MatchStatus.COMPLETED)
                        )
                        .fetchOne()
        ).orElse(0L);

        long plannedCount = Optional.ofNullable(
                queryFactory
                        .select(settlement.count())
                        .from(settlement)
                        .where(
                                settlement.serviceMatch.caregiver.caregiverId.eq(caregiverId),
                                settlement.serviceMatch.matchStatus.eq(MatchStatus.PENDING)
                        )
                        .fetchOne()
        ).orElse(0L);

        long cancelledCount = Optional.ofNullable(
                queryFactory
                        .select(settlement.count())
                        .from(settlement)
                        .where(
                                settlement.serviceMatch.caregiver.caregiverId.eq(caregiverId),
                                settlement.serviceMatch.matchStatus.eq(MatchStatus.CANCELLED)
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

    // 센터에 등록된 요양보호사의 근무 상태, 월-연도별 내역 조회 // 이것도 리팩터링좀 보자
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCenter(
            UUID centerId,
            MatchStatus matchStatus,   // nullable
            Integer year,        // nullable
            Integer month        // nullable
    ) {
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiverCenter.center.centerId.eq(centerId));

        // 상태 필터
        if (matchStatus != null) {
            where.and(settlement.serviceMatch.matchStatus.eq(matchStatus));
        } else {
            where.and(settlement.serviceMatch.matchStatus.eq(MatchStatus.COMPLETED));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(settlement.serviceMatch.serviceDate.year().eq(year)
                    .and(settlement.serviceMatch.serviceDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        settlement.serviceMatch.serviceDate,
                        settlement.serviceMatch.serviceStartTime,
                        settlement.serviceMatch.serviceEndTime,
                        settlement.settlementAmount,
                        settlement.serviceMatch.matchStatus
                ))
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(where)
                .orderBy(
                        settlement.modifiedAt
                                .coalesce(settlement.createdAt)
                                .desc()
                )
                .fetch();
    }

    // 개별 요양보호사의 근무 상태, 월-연도별 내역 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCaregiver(
            UUID caregiverId,
            MatchStatus matchStatus,   // nullable
            Integer year,        // nullable
            Integer month        // nullable
    ) {
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QUser user = QUser.user;

        BooleanBuilder where = new BooleanBuilder();
        where.and(caregiver.caregiverId.eq(caregiverId));

        // 상태 필터
        if (matchStatus != null) {
            where.and(settlement.serviceMatch.matchStatus.eq(matchStatus));
        }

        // 연/월 필터
        if (year != null && month != null) {
            where.and(settlement.serviceMatch.serviceDate.year().eq(year)
                    .and(settlement.serviceMatch.serviceDate.month().eq(month)));
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverWorkResponse.class,
                        user.name,
                        settlement.serviceMatch.serviceDate,
                        settlement.serviceMatch.serviceStartTime,
                        settlement.serviceMatch.serviceEndTime,
                        settlement.settlementAmount,
                        settlement.serviceMatch.matchStatus
                ))
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(where)
                .orderBy(
                        settlement.modifiedAt
                                .coalesce(settlement.createdAt)
                                .desc()
                )
                .fetch();
    }

    // 센터의 6개월 간 정산 내역 표시
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId, int monthsBack) {
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.minusMonths(monthsBack - 1).withDayOfMonth(1);

        List<GetMonthlyPaymentResponse> rawResults = queryFactory
                .select(Projections.constructor(
                        GetMonthlyPaymentResponse.class,
                        settlement.serviceMatch.serviceDate.year(),
                        settlement.serviceMatch.serviceDate.month(),
                        settlement.settlementAmount.sum()
                ))
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(settlement.isPaid.eq(true)
                        .and(settlement.serviceMatch.matchStatus.eq(MatchStatus.COMPLETED))
                        .and(caregiverCenter.center.centerId.eq(centerId))
                        .and(settlement.serviceMatch.serviceDate.goe(startMonth)))
                .groupBy(settlement.serviceMatch.serviceDate.year(), settlement.serviceMatch.serviceDate.month())
                .orderBy(settlement.serviceMatch.serviceDate.year().desc(), settlement.serviceMatch.serviceDate.month().desc())
                .fetch();

        Map<String, BigDecimal> map = rawResults.stream()
                .collect(Collectors.toMap(
                        r -> r.year() + "-" + r.month(),
                        GetMonthlyPaymentResponse::totalAmount
                ));

        List<GetMonthlyPaymentResponse> filled = new ArrayList<>();
        for (int i = 0 ; i <= monthsBack; i++) { // 최신 월부터
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
        QSettlement settlement = QSettlement.settlement;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 최근 7일

        // DB에서 미정산 내역 조회
        List<GetDailyUnsettledResponse> rawResults = queryFactory
                .select(Projections.constructor(
                        GetDailyUnsettledResponse.class,
                        settlement.serviceMatch.serviceDate,
                        settlement.settlementId.count().coalesce(0L),
                        settlement.settlementAmount.sum().coalesce(BigDecimal.ZERO)
                ))
                .from(settlement)
                .join(settlement.serviceMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(settlement.isPaid.eq(false)
                        .and(settlement.serviceMatch.matchStatus.eq(MatchStatus.COMPLETED))
                        .and(caregiverCenter.center.centerId.eq(centerId))
                        .and(settlement.serviceMatch.serviceDate.between(startDate, today))
                )
                .groupBy(settlement.serviceMatch.serviceDate)
                .orderBy(settlement.serviceMatch.serviceDate.asc())
                .fetch();

        // 날짜별 map 생성
        Map<LocalDate, GetDailyUnsettledResponse> map = rawResults.stream()
                .collect(Collectors.toMap(GetDailyUnsettledResponse::date, r -> r));

        // 누락된 날짜 채우기
        List<GetDailyUnsettledResponse> filled = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate date = startDate.plusDays(i);
            filled.add(map.getOrDefault(date, new GetDailyUnsettledResponse(date, 0L, BigDecimal.ZERO)));
        }

        return filled;
    }

    // 이번 달 누적 정산 금액 조회
    public BigDecimal getTotalSettledAmountThisMonth(UUID centerId) {
        QSettlement settlement = QSettlement.settlement;

        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);

        BigDecimal total = queryFactory
                .select(settlement.settlementAmount.sum())
                .from(settlement)
                .where(
                        settlement.isPaid.eq(true)
                                .and(settlement.serviceMatch.serviceDate.goe(firstDay))
                                .and(settlement.caregiverCenter.center.centerId.eq(centerId))
                )
                .fetchOne();

        return total != null ? total : BigDecimal.ZERO;
    }

    // 미정산 건수 조회
    public Long countUnsettled(UUID centerId) {
        QSettlement settlement = QSettlement.settlement;

        return queryFactory
                .select(settlement.countDistinct()) // 혹은 countDistinct(settlement.id)로 명시
                .from(settlement)
                .where(
                        settlement.isPaid.eq(false)
                                .and(settlement.caregiverCenter.center.centerId.eq(centerId))
                )
                .fetchOne();
    }
}
