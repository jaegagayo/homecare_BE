package jaega.homecare.domain.caregiverCenter.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.center.entity.QCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CaregiverCenterQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Caregiver> findAllByStatus(CaregiverStatus caregiverStatus) {
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(caregiver)
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver)
                .where(caregiverCenter.status.eq(caregiverStatus))
                .fetch();
    }

    public Optional<CaregiverCenter> findByCenterIdAndCaregiverId(UUID centerId, UUID caregiverId) {
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QCenter center = QCenter.center;
        QCaregiver caregiver = QCaregiver.caregiver;

        CaregiverCenter result = queryFactory
                .selectFrom(caregiverCenter)
                .join(caregiverCenter.center, center).fetchJoin()
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .where(
                        center.centerId.eq(centerId),
                        caregiver.caregiverId.eq(caregiverId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
