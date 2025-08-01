package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.serviceMatch.dto.res.ServiceMatchNotificationResponse;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServiceMatchQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ServiceMatchNotificationResponse> findMatchesByCenterId(UUID centerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(Projections.constructor(
                        ServiceMatchNotificationResponse.class,
                        serviceRequest.user.name,
                        caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.startTime,
                        serviceMatch.endTime,
                        serviceRequest.serviceType.stringValue(),
                        serviceMatch.status
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceMatch.caregiver, caregiver)
                .where(caregiver.center.centerId.eq(centerId))
                .fetch();
    }
}