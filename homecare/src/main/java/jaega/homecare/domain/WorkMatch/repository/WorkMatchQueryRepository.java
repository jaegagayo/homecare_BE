package jaega.homecare.domain.WorkMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkMatchQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final CaregiverRepository caregiverRepository;

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
}
