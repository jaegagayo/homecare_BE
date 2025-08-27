package jaega.homecare.domain.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.review.entity.QReview;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
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
}
