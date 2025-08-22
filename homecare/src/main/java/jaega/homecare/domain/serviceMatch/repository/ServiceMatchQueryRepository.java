package jaega.homecare.domain.serviceMatch.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.entity.QCaregiverCenter;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.settlement.dto.res.GetDashboardWorkStatusResponse;
import jaega.homecare.domain.settlement.dto.res.WorkPlaceDistribution;
import jaega.homecare.domain.users.entity.QUser;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.settlement.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

        QUser caregiverUser = new QUser("caregiverUser");
        QUser consumerUser = new QUser("consumerUser");

        // 1. 기본 정보 조회 (serviceTypes는 비워둠)
        List<GetCaregiverMatchesResponse> baseList = queryFactory
                .select(Projections.constructor(
                        GetCaregiverMatchesResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.caregiverId,
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
                .join(serviceRequest.consumer, consumer)
                .join(consumer.user, consumerUser)
                .where(caregiver.caregiverId.eq(caregiverId))
                .orderBy(serviceMatch.id.desc())
                .fetch();

        System.out.println(baseList+"123");

        // 2. caregiverIds 추출
        Set<UUID> caregiverIds = baseList.stream()
                .map(GetCaregiverMatchesResponse::caregiverId)
                .collect(Collectors.toSet());

        if (caregiverIds.isEmpty()) {
            return baseList; // 결과 없으면 바로 반환
        }

        // 3. serviceTypes 조회
        List<Object[]> rows = caregiverRepository.findServiceTypesByCaregiverIds(caregiverIds);

        // 4. Map<Long, Set<ServiceType>> 변환
        Map<UUID, Set<ServiceType>> serviceTypeMap = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> (UUID) row[0],
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

    /**
     *  대시보드
     */


    public DashboardStats getDashboardStats(UUID centerId, LocalDate date) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        List<Tuple> result = queryFactory
                .select(
                        caregiver.countDistinct(), // 센터의 오늘 매칭이 배정, 완료된 요양보호사 수 조회
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(serviceMatch.caregiver.countDistinct())
                                        .from(serviceMatch)
                                        .join(serviceMatch.caregiver, caregiver)
                                        .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                                        .where(
                                                caregiverCenter.center.centerId.eq(centerId)
                                                        .and(serviceMatch.serviceDate.eq(date))
                                                        .and(serviceMatch.matchStatus.in(MatchStatus.PENDING, MatchStatus.COMPLETED))
                                        ), "assignedCaregivers"
                        ),
                        ExpressionUtils.as( // 매칭이 있으나 오늘이 아닌 요양보호사 수 조회
                                JPAExpressions
                                        .select(serviceMatch.count())
                                        .from(serviceMatch)
                                        .where(serviceMatch.matchStatus.eq(MatchStatus.PENDING)
                                                .and(serviceMatch.serviceDate.eq(date))), "waitingApplicants"
                        )
                )
                .from(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(caregiverCenter.center.centerId.eq(centerId))
                .fetch();

        Tuple row = result.get(0);
        return new DashboardStats(row.get(0, Long.class), row.get(1, Long.class), row.get(2, Long.class));
    }
    // 요양 보호사 대시보드의 근무지 별 분포 통계 조회
    public List<WorkPlaceDistribution> getWorkPlaceDistributionByServiceType(UUID centerId) {
        QCaregiver caregiver = QCaregiver.caregiver;
        QCaregiverCenter caregiverCenter = QCaregiverCenter.caregiverCenter;

        // 센터 소속 활동 중인 요양보호사 조회 (fetch join으로 serviceTypes 포함)
        List<Caregiver> caregivers = queryFactory
                .selectFrom(caregiver)
                .join(caregiverCenter).on(caregiverCenter.caregiver.eq(caregiver))
                .where(
                        caregiverCenter.center.centerId.eq(centerId),
                        caregiverCenter.status.eq(CaregiverStatus.ACTIVE)
                )
                .fetch();

        if (caregivers.isEmpty()) {
            return Collections.emptyList();
        }

        // ServiceType별 카운트 계산
        Map<ServiceType, Long> serviceTypeCount = new HashMap<>();
        for (Caregiver cg : caregivers) {
            Set<ServiceType> serviceTypes = cg.getServiceTypes(); // 이미 Set<ServiceType>
            if (serviceTypes != null) {
                for (ServiceType st : serviceTypes) {
                    serviceTypeCount.put(st, serviceTypeCount.getOrDefault(st, 0L) + 1);
                }
            }
        }

        // 총합 계산
        long total = serviceTypeCount.values().stream().mapToLong(Long::longValue).sum();

        // DTO 변환
        return serviceTypeCount.entrySet().stream()
                .map(entry -> {
                    double percent = total > 0 ? (double) entry.getValue() * 100 / total : 0.0;
                    percent = Math.round(percent * 10) / 10.0; // 소수점 첫째 자리까지
                    return new WorkPlaceDistribution(
                            entry.getKey(),
                            entry.getValue(),
                            percent
                    );
                })
                .toList();
    }
}