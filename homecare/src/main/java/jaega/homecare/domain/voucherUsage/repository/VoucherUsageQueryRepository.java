package jaega.homecare.domain.voucherUsage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.voucherUsage.entity.QVoucherUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VoucherUsageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Long findTotalUsageAmountByVoucherId(UUID voucherId) {
        QVoucherUsage voucherUsage = QVoucherUsage.voucherUsage;

        return queryFactory
                .select(voucherUsage.amount.sum())
                .from(voucherUsage)
                .where(voucherUsage.voucher.voucherId.eq(voucherId))
                .fetchOne();
    }


}
