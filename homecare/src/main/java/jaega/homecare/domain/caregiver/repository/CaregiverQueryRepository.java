package jaega.homecare.domain.caregiver.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCertification;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.entity.QCaregiverPreference;
import jaega.homecare.domain.center.dto.req.SearchCaregiverResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.center.dto.res.GetCaregiverByStatusResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverByServiceTypeResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class CaregiverQueryRepository {
    private final JPAQueryFactory queryFactory;

    // 센터의 모든 요양보호사 목록 조회
    public List<GetCaregiverResponse> findAllByCenterId(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QCaregiverPreference preference = QCaregiverPreference.caregiverPreference;

        // Caregiver + Preference fetch 조인
        List<Tuple> results = queryFactory
                .select(caregiverCenter, caregiver, preference)
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver).fetchJoin()
                .join(caregiver.user, user).fetchJoin()
                .leftJoin(preference).on(preference.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Caregiver c = tuple.get(caregiver);
                    CaregiverPreference pref = tuple.get(preference);

                    // 선호 서비스 타입만 가져오기
                    Set<ServiceType> serviceTypes = pref != null && pref.getServiceTypes() != null
                            ? pref.getServiceTypes()
                            : Collections.emptySet();

                    return new GetCaregiverResponse(
                            c.getCaregiverId(),
                            c.getUser().getName(),
                            c.getUser().getPhone(),
                            serviceTypes,
                            tuple.get(caregiverCenter).getStatus()
                    );
                })
                .toList();
    }

    // 센터의 요양보호사 근무 상태 기반 목록 조회
    public List<GetCaregiverByStatusResponse> findCaregiverByCaregiverStatus(
            UUID centerId, CaregiverStatus status
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
                                .and(caregiverCenter.status.eq(status))
                )
                .fetch();

        return caregiverCenters.stream()
                .map(cc -> new GetCaregiverByStatusResponse(
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
        QCaregiverPreference preference = QCaregiverPreference.caregiverPreference;

        List<Tuple> results = queryFactory
                .select(caregiver, preference)
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver)
                .join(caregiver.user, user)
                .leftJoin(preference).on(preference.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetch();

        return results.stream()
                .filter(tuple -> {
                    CaregiverPreference pref = tuple.get(preference);
                    if (pref == null || pref.getServiceTypes() == null) return false;

                    // Preference 서비스 타입과 요청한 serviceTypes 간 교집합 확인
                    return !Collections.disjoint(pref.getServiceTypes(), serviceTypes);
                })
                .map(tuple -> {
                    Caregiver c = tuple.get(caregiver);
                    CaregiverPreference pref = tuple.get(preference);

                    return new GetCaregiverByServiceTypeResponse(
                            c.getUser().getName(),
                            pref.getServiceTypes()
                    );
                })
                .toList();
    }

    //  이번 달 신규 요양보호사 카운트
    public Long countNewCaregiversThisMonth(Center center) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(caregiver.countDistinct())
                .from(caregiverCenter)
                .join(caregiverCenter.caregiver, caregiver)
                .where(
                        caregiverCenter.center.centerId.eq(center.getCenterId())
                                .and(Expressions.booleanTemplate(
                                        "function('date_trunc', 'month', {0}) = function('date_trunc', 'month', current_date)",
                                        caregiver.createdAt
                                ))
                )
                .fetchOne();
    }

    // 특정 센터에 소속한 모든 요양보호사 카운트
    public Long countByCenterId(UUID centerId) {
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(caregiverCenter.count())
                .from(caregiverCenter)
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetchOne();
    }

    // 특정 센터에 소속한 요양보호사를 상태 별로 카운트
    public Long countByCenterAndStatus(UUID centerId, CaregiverStatus status) {
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        return queryFactory
                .select(caregiverCenter.count())
                .from(caregiverCenter)
                .where(
                        caregiverCenter.center.centerId.eq(centerId)
                                .and(caregiverCenter.status.eq(status))
                )
                .fetchOne();
    }


    public List<Tuple> findServiceTypesByCaregiverIds(Set<UUID> caregiverIds) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverPreference pref = QCaregiverPreference.caregiverPreference;

        return queryFactory
                .select(caregiver.caregiverId, pref.serviceTypes)
                .from(caregiver)
                .leftJoin(pref).on(pref.caregiver.eq(caregiver))
                .where(caregiver.caregiverId.in(caregiverIds))
                .fetch();
    }

    public List<SearchCaregiverResponse> searchByNameOrPhone(String keyword) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;
        QCertification certification = QCertification.certification;

        return queryFactory
                .select(Projections.constructor(SearchCaregiverResponse.class,
                        caregiver.caregiverId,
                        user.name,
                        user.phone,
                        certification.CertificationNumber
                ))
                .from(caregiver)
                .join(caregiver.user, user)
                .leftJoin(certification).on(certification.caregiver.eq(caregiver))
                .where(user.name.containsIgnoreCase(keyword)
                        .or(user.phone.containsIgnoreCase(keyword)))
                .fetch();
    }
}