package jaega.homecare.domain.WorkLog.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.entity.QWorkLog;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
                .join(caregiver.user, user)
                .where(workMatch.workDate.eq(date)
                        .and(caregiver.center.centerId.eq(centerId)))
                .fetch();
    }

    public List<GetWorkLogByPaid> findWorkLogsByPaid(UUID centerId, Boolean isPaid) {
        QWorkLog workLog = QWorkLog.workLog;
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

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
                .where(workLog.isPaid.eq(isPaid)
                        .and(caregiver.center.centerId.eq(centerId)))
                .fetch();
    }
}
