package jaega.homecare.domain.caregiver.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CaregiverQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<GetCaregiverResponse> findAllByCenterId(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        List<Caregiver> caregivers = queryFactory
                .selectFrom(caregiver).distinct()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(caregiver.center.centerId.eq(centerId))
                .fetch();

        // 각 Caregiver -> Dto
        return caregivers.stream()
                .map(c -> new GetCaregiverResponse(
                        c.getCaregiverId(),
                        c.getUser().getName(),
                        c.getUser().getPhone(),
                        c.getServiceTypes()
                ))
                .toList();
    }
}