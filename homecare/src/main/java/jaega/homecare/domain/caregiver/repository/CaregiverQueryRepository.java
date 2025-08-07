package jaega.homecare.domain.caregiver.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
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
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> results = queryFactory
                .select(caregiver, caregiverCenter.status)
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Caregiver c = tuple.get(caregiver);
                    CaregiverStatus status = tuple.get(caregiverCenter.status);

                    return new GetCaregiverResponse(
                            c.getCaregiverId(),
                            c.getUser().getName(),
                            c.getUser().getPhone(),
                            c.getServiceTypes(),
                            status
                    );
                })
                .toList();
    }

    public List<GetCaregiverByCaregiverStatusResponse> findCaregiverByCaregiverStatus(UUID centerId, CaregiverStatus status) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> results = queryFactory
                .select(caregiver, caregiverCenter.status)
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(caregiverCenter.center.centerId.eq(centerId)
                        .and(caregiverCenter.status.eq(status)))
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Caregiver c = tuple.get(caregiver);
                    CaregiverStatus caregiverStatus = tuple.get(caregiverCenter.status);

                    return new GetCaregiverByCaregiverStatusResponse(
                            c.getUser().getName(),
                            caregiverStatus
                    );
                })
                .toList();
    }

    public List<GetCaregiverByServiceTypeResponse> findCaregiverByServiceTypes(UUID centerId, Set<ServiceType> serviceTypes){
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Caregiver> caregivers = queryFactory
                .selectFrom(caregiver).distinct()
                .join(caregiver.user, user).fetchJoin()
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
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