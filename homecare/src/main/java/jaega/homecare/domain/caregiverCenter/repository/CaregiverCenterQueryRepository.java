package jaega.homecare.domain.caregiverCenter.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
