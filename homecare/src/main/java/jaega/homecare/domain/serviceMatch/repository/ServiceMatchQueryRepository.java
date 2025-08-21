package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.workLog.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ServiceMatchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CaregiverRepository caregiverRepository;

//    // Center의
//    public List<GetServiceMatchByCenterResponse> findMatchesByCenterId(UUID centerId) {
//        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
//        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
//        QCaregiver caregiver = QCaregiver.caregiver;
//        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;
//        QConsumer consumer = QConsumer.consumer;
//
//        QUser caregiverUser = caregiver.user;
//        QUser consumerUser = consumer.user;
//
//        return queryFactory
//                .select(Projections.constructor(
//                        GetServiceMatchByCenterResponse.class,
//                        consumerUser.name,
//                        caregiverUser.name,
//                        serviceMatch.serviceDate,
//                        serviceMatch.serviceStartTime,
//                        serviceMatch.serviceEndTime,
//                        serviceRequest.serviceType.stringValue(),
//                        serviceMatch.matchStatus
//                ))
//                .from(serviceMatch)
//                .join(serviceMatch.serviceRequest, serviceRequest)
//                .join(serviceRequest.consumer.user, consumerUser)
//                .join(serviceMatch.caregiver.user, caregiverUser)
//                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
//                .where(caregiverCenter.center.centerId.eq(centerId))
//                .orderBy(serviceMatch.serviceDate.desc())
//                .fetch();
//    }

    public List<GetCaregiverMatchesResponse> findByCaregiverId(UUID caregiverId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QConsumer consumer = QConsumer.consumer;

        QUser caregiverUser = caregiver.user;
        QUser consumerUser = consumer.user;

        // 1. 기본 정보 조회 (serviceTypes는 비워둠)
        List<GetCaregiverMatchesResponse> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.id,
                        caregiverUser.name,
                        consumerUser.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        Expressions.constant(Collections.emptySet()), // ServiceType, 이후 별도 로딩
                        serviceRequest.serviceAddress,
                        Expressions.constant(12000), // TODO: 하드코딩, 시급(추후 제거 필요)
                        serviceMatch.matchStatus,
                        Expressions.nullExpression(String.class) // notes, 추가 내용
                ))
                .from(serviceMatch)
                .join(serviceMatch.caregiver, caregiver)
                .join(caregiver.user, caregiverUser)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer.user, consumerUser)
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
                        base.serviceStartTime(),
                        base.serviceEndTime(),
                        serviceTypeMap.getOrDefault(base.caregiverId(), Collections.emptySet()),
                        base.address(),
                        base.hourlyWage(),
                        base.status(),
                        base.notes()
                ))
                .toList();
    }
}