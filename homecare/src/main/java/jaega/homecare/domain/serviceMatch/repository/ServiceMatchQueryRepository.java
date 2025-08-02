package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByConsumerResponse;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ServiceMatchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CaregiverRepository caregiverRepository;

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
                .join(serviceRequest.user, requesterUser)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
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
                .where(requesterUser.userId.eq(userId))
                .orderBy(serviceMatch.serviceDate.desc())
                .fetch();
    }

    public List<GetCaregiverMatchesResponse> findByCaregiverId(UUID caregiverId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser user = QUser.user;

        // 1. 기본 정보만 조회, serviceTypes는 null로 둠
        List<GetCaregiverMatchesResponse> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.id,
                        caregiver.user.name,
                        serviceMatch.serviceRequest.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.startTime,
                        serviceMatch.endTime,
                        Expressions.constant(Collections.emptySet()),
                        serviceRequest.address,
                        Expressions.constant(12000),
                        serviceMatch.status,
                        Expressions.nullExpression(String.class)
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, user)
                .where(caregiver.caregiverId.eq(caregiverId))
                .orderBy(serviceMatch.id.desc())
                .fetch();

        // 2. caregiverIds 추출
        Set<Long> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesResponse::caregiverId)
                .collect(Collectors.toSet());

        // 3. JPQL로 serviceTypes 조회
        List<Object[]> rows = caregiverRepository.findServiceTypesByIds(caregiverIds);

        // 4. Map<Long, Set<ServiceType>> 변환
        Map<Long, Set<ServiceType>> serviceTypeMap = new HashMap<>();
        for (Object[] row : rows) {
            Long cId = (Long) row[0];
            ServiceType st = (ServiceType) row[1];
            serviceTypeMap.computeIfAbsent(cId, k -> new HashSet<>()).add(st);
        }

        // 5. DTO 객체 재생성 (record면 새 객체 생성 필요)
        List<GetCaregiverMatchesResponse> resultList = baseList.stream()
                .map(base -> new GetCaregiverMatchesResponse(
                        base.serviceMatchId(),
                        base.caregiverId(),
                        base.caregiverName(),
                        base.consumerName(),
                        base.serviceDate(),
                        base.startTime(),
                        base.endTime(),
                        serviceTypeMap.getOrDefault(base.caregiverId(), Collections.emptySet()),
                        base.address(),
                        base.hourlyWage(),
                        base.status(),
                        base.notes()
                ))
                .collect(Collectors.toList());

        return resultList;
    }
}