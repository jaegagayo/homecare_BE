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
