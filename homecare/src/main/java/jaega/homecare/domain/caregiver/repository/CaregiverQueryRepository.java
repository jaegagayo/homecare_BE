package jaega.homecare.domain.caregiver.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.center.dto.res.GetCaregiverByCaregiverStatusResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverByServiceTypeResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
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
                        c.getServiceTypes(),
                        c.getStatus()
                ))
                .toList();
    }

    public List<GetCaregiverByCaregiverStatusResponse> findCaregiverByCaregiverStatus(UUID centerId, CaregiverStatus status){
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        List<Caregiver> caregivers = queryFactory
                .selectFrom(caregiver).distinct()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(caregiver.center.centerId.eq(centerId)
                        .and(caregiver.status.eq(status)))
                .fetch();

        return caregivers.stream()
                .map(c -> new GetCaregiverByCaregiverStatusResponse(
                        c.getUser().getName(),
                        c.getStatus()
                ))
                .toList();
    }

    public List<GetCaregiverByServiceTypeResponse> findCaregiverByServiceTypes(UUID centerId, Set<ServiceType> serviceTypes){
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        List<Caregiver> caregivers = queryFactory
                .selectFrom(caregiver).distinct()
                .join(caregiver.user, user).fetchJoin()
                .where(
                        caregiver.center.centerId.eq(centerId)
                                .and(caregiver.serviceTypes.any().in(serviceTypes))
                )
                .fetch();

        return caregivers.stream()
                .map(c -> new GetCaregiverByServiceTypeResponse(
                        c.getUser().getName(),
                        c.getServiceTypes()
                ))
                .toList();
    }
}