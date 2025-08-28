package jaega.homecare.domain.consumer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.caregiver.entity.QCaregiver;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerNextScheduleResponse;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.review.entity.QReview;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.users.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ConsumerQueryRepository {

}
