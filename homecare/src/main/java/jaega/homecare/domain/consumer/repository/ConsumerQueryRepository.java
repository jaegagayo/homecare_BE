package jaega.homecare.domain.consumer.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.review.entity.QReview;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsumerQueryRepository {
    private final JPAQueryFactory queryFactory;

    // Consumer의 주간 스케줄 조회
    public List<ConsumerScheduleResponse> findWeeklySchedule(UUID consumerId, LocalDate weekStart, LocalDate weekEnd) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;

        return queryFactory
                .select(Projections.constructor(
                        ConsumerScheduleResponse.class,
                        serviceRequest.serviceRequestId,
                        caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .join(serviceMatch.caregiver, caregiver)
                .where(
                        consumer.consumerId.eq(consumerId),
                        serviceMatch.serviceDate.between(weekStart, weekEnd)
                )
                .orderBy(serviceMatch.serviceDate.asc(), serviceMatch.serviceStartTime.asc())
                .fetch();

    }

    // Consumer의 일정 상세 조회
    public ConsumerScheduleDetailResponse findScheduleDetail(UUID serviceRequestId) {
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QConsumer consumer = QConsumer.consumer;
        QCaregiver caregiver = QCaregiver.caregiver;
        QUser caregiverUser = caregiver.user;
        QReview review = QReview.review;

        return queryFactory.
                select(Projections.constructor(
                        ConsumerScheduleDetailResponse.class,
                        caregiverUser.name,
                        caregiverUser.phone,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.duration,
                        serviceRequest.serviceAddress,
                        serviceRequest.serviceType,
                        serviceMatch.matchStatus,
                        review.id.isNotNull() // 리뷰 존재 여부
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceRequest.consumer, consumer)
                .join(serviceMatch.caregiver, caregiver)
                .leftJoin(review).on(review.serviceMatch.eq(serviceMatch))
                .where(serviceRequest.serviceRequestId.eq(serviceRequestId))
                .fetchOne();
    }
}
