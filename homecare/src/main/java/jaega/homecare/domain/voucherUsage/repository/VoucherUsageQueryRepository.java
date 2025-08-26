package jaega.homecare.domain.voucherUsage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.voucher.entity.QVoucher;
import jaega.homecare.domain.voucherUsage.entity.QVoucherUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VoucherUsageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Long findTotalUsageAmountByVoucherId(UUID voucherId, ServiceRequestStatus requestStatus) {
        QVoucherUsage voucherUsage = QVoucherUsage.voucherUsage;
        QServiceRequest serviceRequest = QServiceRequest.serviceRequest;

        return queryFactory
                .select(voucherUsage.amount.sum())
                .from(voucherUsage)
                .join(voucherUsage.serviceMatch.serviceRequest, serviceRequest)
                .where(voucherUsage.voucher.voucherId.eq(voucherId),
                        serviceRequest.requestStatus.eq(requestStatus)
                )
                .fetchOne();
    }


}
