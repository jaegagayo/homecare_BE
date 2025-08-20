package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
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
        /*
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
        QUser requesterUser = QConsumer.user;
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
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .orderBy(serviceMatch.serviceDate.desc())
                .fetch();

         */
        return null;
    }

    public List<GetServiceMatchByConsumerResponse> findByUserId(UUID userId) {
        /*
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
                        caregiver.address,
                        caregiverUser.phone,
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

         */
        return null;
    }

    public List<GetCaregiverMatchesResponse> findByCaregiverId(UUID caregiverId) {
        /*
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser caregiverUser = new QUser("caregiverUser");
        QUser consumerUser = new QUser("consumerUser");

        // 1. 기본 정보 조회 (serviceTypes는 비워둠)
        List<GetCaregiverMatchesResponse> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.id,
                        caregiverUser.name,
                        consumerUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.startTime,
                        serviceMatch.endTime,
                        Expressions.constant(Collections.emptySet()), // ServiceType, 이후 별도 로딩
                        serviceRequest.address,
                        Expressions.constant(12000), // TODO: 하드코딩, 시급(추후 제거 필요)
                        serviceMatch.status,
                        Expressions.nullExpression(String.class) // notes, 추가 내용
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.user, consumerUser)
                .where(caregiver.caregiverId.eq(caregiverId))
                .orderBy(serviceMatch.id.desc())
                .fetch();

        // 2. caregiverIds 추출
        Set<Long> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesResponse::caregiverId)
                .collect(Collectors.toSet());

        if (caregiverIds.isEmpty()) {
            return baseList; // 결과 없으면 바로 반환
        }

        // 3. serviceTypes 조회
        List<Object[]> rows = caregiverRepository.findServiceTypesByIds(caregiverIds);

        // 4. Map<Long, Set<ServiceType>> 변환
        Map<Long, Set<ServiceType>> serviceTypeMap = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (ServiceType) row[1], Collectors.toSet())
                ));

        // 5. serviceTypes 채워서 새 DTO 생성
        return baseList.stream()
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
                .toList();

         */

        return null;
    }
}