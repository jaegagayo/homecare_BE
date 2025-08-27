package jaega.homecare.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
import jaega.homecare.domain.review.entity.QReview;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Review> findWrittenReviewsByConsumer(UUID consumerId) {
        QReview review = QReview.review;
        QServiceMatch sm = QServiceMatch.serviceMatch;

        return queryFactory
                .selectFrom(review)
                .join(review.serviceMatch, sm).fetchJoin()
                .where(sm.serviceRequest.consumer.consumerId.eq(consumerId))
                .fetch();
    }

    // 완료된 일정 중 리뷰가 없는 일정 조회
    public List<ConsumerPendingReviewResponse> findCompletedSchedulesWithoutReview(UUID consumerId) {
        QServiceMatch serviceMatch = QServiceMatch.serviceMatch;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;
        QCaregiver caregiver = QCaregiver.caregiver;
        QReview review = QReview.review;

        return queryFactory
                .select(Projections.constructor(
                        ConsumerPendingReviewResponse.class,
                        serviceMatch.serviceMatchId,
                        caregiver.user.name,
                        serviceMatch.serviceDate,
                        serviceMatch.serviceStartTime,
                        serviceMatch.serviceEndTime,
                        serviceRequest.serviceType
                ))
                .from(serviceMatch)
                .join(serviceMatch.serviceRequest, serviceRequest)
                .join(serviceMatch.caregiver, caregiver)
                .leftJoin(review).on(review.serviceMatch.eq(serviceMatch))
                .where(
                        serviceRequest.consumer.consumerId.eq(consumerId),
                        serviceMatch.matchStatus.eq(MatchStatus.COMPLETED),
                        review.id.isNull()
                )
                .orderBy(serviceMatch.serviceDate.desc(), serviceMatch.serviceStartTime.desc())
                .fetch();

    }
}
