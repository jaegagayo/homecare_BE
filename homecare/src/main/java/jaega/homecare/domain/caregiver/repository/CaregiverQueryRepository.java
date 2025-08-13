package jaega.homecare.domain.caregiver.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
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

    // 센터의 모든 요양보호사 목록 조회
    public List<GetCaregiverResponse> findAllByCenterId(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<CaregiverCenter> caregiverCenters = queryFactory
                .selectFrom(caregiverCenter).distinct()
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetch();

        return caregiverCenters.stream()
                .map(cc -> {
                    Caregiver c = cc.getCaregiver();
                    return new GetCaregiverResponse(
                            c.getCaregiverId(),
                            c.getUser().getName(),
                            c.getUser().getPhone(),
                            c.getServiceTypes(),
                            cc.getStatus()
                    );
                })
                .toList();
    }

    // 센터의 요양보호사 근무 상태 기반 목록 조회
    public List<GetCaregiverByCaregiverStatusResponse> findCaregiverByCaregiverStatus(
            UUID centerId, CaregiverStatus status
    ) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<CaregiverCenter> caregiverCenters = queryFactory
                .selectFrom(caregiverCenter).distinct()
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(caregiver.serviceTypes).fetchJoin()
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
                                .and(caregiverCenter.status.eq(status))
                )
                .fetch();

        return caregiverCenters.stream()
                .map(cc -> new GetCaregiverByCaregiverStatusResponse(
                        cc.getCaregiver().getUser().getName(),
                        cc.getStatus()
                ))
                .toList();
    }

    // 서비스 유형 기반 요양보호사 목록 조회
    public List<GetCaregiverByServiceTypeResponse> findCaregiverByServiceTypes(
            UUID centerId, Set<ServiceType> serviceTypes
    ) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<CaregiverCenter> caregiverCenters = queryFactory
                .selectFrom(caregiverCenter).distinct()
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
                                .and(caregiver.serviceTypes.any().in(serviceTypes))
                )
                .fetch();

        return caregiverCenters.stream()
                .map(cc -> new GetCaregiverByServiceTypeResponse(
                        cc.getCaregiver().getUser().getName(),
                        cc.getCaregiver().getServiceTypes()
                ))
                .toList();
    }

    public Long countNewCaregiversThisMonth(Center center) {
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(caregiver.count())
                .from(caregiver)
                .where(
                        caregiver.center.centerId.eq(center.getCenterId())
                                .and(Expressions.booleanTemplate(
                                        "function('date_trunc', 'month', {0}) = function('date_trunc', 'month', current_date)",
                                        caregiver.createdAt
                                ))
                )
                .fetchOne();
    }

}