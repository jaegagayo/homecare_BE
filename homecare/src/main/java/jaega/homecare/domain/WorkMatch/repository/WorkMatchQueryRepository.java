package jaega.homecare.domain.WorkMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class WorkMatchQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<GetCaregiverMatchesByMonth> findWorkMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        LocalDate startDate;
        LocalDate endDate;

        if (day != null) {
            // ✅ 특정 일만 조회
            startDate = LocalDate.of(year, month, day);
            endDate = startDate;
        } else {
            // ✅ 월 전체 조회
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesByMonth.class,
                        workMatch.workMatchId,
                        user.name,
                        workMatch.workDate,
                        workMatch.status
                ))
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .where(
                        workMatch.workDate.between(startDate, endDate),
                        caregiver.center.centerId.eq(centerId)
                )
                .orderBy(workMatch.workDate.desc())
                .fetch();
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
}
