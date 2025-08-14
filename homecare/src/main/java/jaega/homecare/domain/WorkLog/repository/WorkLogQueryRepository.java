package jaega.homecare.domain.WorkLog.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.entity.QWorkLog;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkLogQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<GetWorkLogByDateResponse> findWorkLogsByDate(UUID centerId, LocalDate date) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetWorkLogByDateResponse.class,
                        workLog.workLogId,
                        workMatch.workDate,
                        workLog.workTime_start,
                        workLog.workTime_end,
                        caregiver.user.name
                ))
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workMatch.workDate.eq(date)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(workLog.workTime_start.asc())
                .fetch();
    }

    public List<GetWorkLogByPaid> findWorkLogsByPaid(UUID centerId, Boolean isPaid) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(Projections.constructor(
                        GetWorkLogByPaid.class,
                        workLog.workLogId,
                        workMatch.workDate,
                        caregiver.user.name
                ))
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(isPaid)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .orderBy(
                        workMatch.workDate.desc())
                .fetch();
    }


    // 이번 달 누적 정산 금액 조회
    public BigDecimal getTotalSettledAmountThisMonth(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);

        return queryFactory
                .select(workLog.settlementAmount.sum())
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(true)
                        .and(workMatch.workDate.goe(firstDay))
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .fetchOne();
    }

    // 미정산 건수 조회
    public Long countUnsettled(UUID centerId) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(workLog.count())
                .from(workLog)
                .join(workLog.workMatch, workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(workLog.isPaid.eq(false)
                        .and(caregiverCenter.center.centerId.eq(centerId)))
                .fetchOne();
    }
}
