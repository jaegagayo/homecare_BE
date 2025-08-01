package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByConsumerResponse;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServiceMatchQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<GetServiceMatchByCenterResponse> findMatchesByCenterId(UUID centerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser requesterUser = QUser.user;
        QUser caregiverUser = new QUser("caregiverUser");

        return queryFactory
                .select(Projections.constructor(
                        GetServiceMatchByCenterResponse.class,
                        requesterUser.name,
                        caregiverUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.startTime,
                        serviceMatch.endTime,
                        serviceRequest.serviceType.stringValue(),
                        serviceMatch.status
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.user, requesterUser)         // 신청자 유저 조인
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)               // 요양보호사 유저 조인
                .where(caregiver.center.centerId.eq(centerId))
                .orderBy(serviceMatch.serviceDate.desc())
                .fetch();
    }

    public List<GetServiceMatchByConsumerResponse> findByUserId(UUID userId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser requesterUser = QUser.user; // 신청자 유저
        QUser caregiverUser = new QUser("caregiverUser"); // 보호사 유저

        return queryFactory
                .select(Projections.constructor(
                        GetServiceMatchByConsumerResponse.class,
                        requesterUser.name,
                        caregiverUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.startTime,
                        serviceMatch.endTime,
                        serviceRequest.serviceType
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.user, requesterUser)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
                .where(requesterUser.userId.eq(userId)) // <- 여기만 바뀜!
                .orderBy(serviceMatch.serviceDate.desc())
                .fetch();
    }
}