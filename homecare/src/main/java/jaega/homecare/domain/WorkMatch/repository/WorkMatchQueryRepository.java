package jaega.homecare.domain.WorkMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.entity.QWorkMatch;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkMatchQueryRepository {
    private final JPAQueryFactory queryFactory;


    public List<GetCaregiverMatchesByMonth> findWorkMatchesByMonth(int year, int month) {
        QWorkMatch workMatch = QWorkMatch.workMatch;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesByMonth.class,
                        user.name,
                        workMatch.workDate,
                        workMatch.status
                ))
                .from(workMatch)
                .join(workMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .where(workMatch.workDate.between(start, end))
                .fetch();
    }
}
