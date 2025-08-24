package jaega.homecare.domain.recurringOffer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.recurringOffer.entity.QRecurringOffer;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RecurringOfferQueryRepository {
    private final JPAQueryFactory queryFactory;

    // 수요자의 아직 '읽지 않은'(정기 제안의 상세 정보) 정기 제안 알림용 조회
    public List<RecurringOffer> findUnreadRecurringOffersByConsumer(UUID consumerId) {
        QRecurringOffer recurringOffer = QRecurringOffer.recurringOffer;

        return queryFactory
                .selectFrom(recurringOffer)
                .where(
                        recurringOffer.consumer.consumerId.eq(consumerId),
                        recurringOffer.recurringOfferUnread.eq(true)       // 알림 확인 안 한 것
                )
                .orderBy(recurringOffer.serviceStartDate.asc()) // 가장 임박한 건(오래된)부터 노출
                .fetch();
    }
}
