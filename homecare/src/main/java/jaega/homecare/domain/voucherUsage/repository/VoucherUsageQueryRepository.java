package jaega.homecare.domain.voucherUsage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.consumer.entity.QConsumer;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.voucher.entity.QVoucher;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucherUsage.entity.QVoucherUsage;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static jaega.homecare.domain.voucher.entity.QVoucher.voucher;
import static jaega.homecare.domain.voucherUsage.entity.QVoucherUsage.voucherUsage;

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

    public List<VoucherUsage> findByVoucherAndStatus(Voucher voucher, MatchStatus status) {
        return queryFactory
                .selectFrom(voucherUsage)
                .join(voucherUsage.serviceMatch)
                .fetchJoin()
                .where(
                        voucherUsage.voucher.eq(voucher),
                        voucherUsage.serviceMatch.matchStatus.eq(status)
                )
                .fetch();
    }

}
