package jaega.homecare.domain.voucherUsage.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.QServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.QServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageCost;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageResponse;
import jaega.homecare.domain.voucherUsage.entity.QVoucherUsage;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

    // 매칭 상태 별 바우처 내역 조회
    public List<VoucherUsage> findByVoucherAndStatusIn(Voucher voucher, List<MatchStatus> statuses) {
        return queryFactory
                .selectFrom(voucherUsage)
                .join(voucherUsage.serviceMatch)
                .fetchJoin()
                .where(
                        voucherUsage.voucher.eq(voucher),
                        voucherUsage.serviceMatch.matchStatus.in(statuses)
                )
                .fetch();
    }


    public VoucherUsageCost findVoucherUsageSummary(Voucher voucher) {
        QVoucherUsage vu = QVoucherUsage.voucherUsage;
        QServiceMatch sm = QServiceMatch.serviceMatch;

        // 상태별 금액 합계 조회
        List<Tuple> sums = queryFactory
                .select(sm.matchStatus, vu.amount.sum(), vu.copay.sum())
                .from(vu)
                .join(vu.serviceMatch, sm)
                .where(vu.voucher.eq(voucher))
                .groupBy(sm.matchStatus)
                .fetch();

        long usedAmount = 0L;
        long expectedAmount = 0L;
        long confirmedCopay = 0L;
        long totalCopay = 0L;

        for (Tuple t : sums) {
            MatchStatus status = t.get(sm.matchStatus);
            Long amountSum = t.get(vu.amount.sum());
            Long copaySum = t.get(vu.copay.sum());

            if (amountSum == null) amountSum = 0L;
            if (copaySum == null) copaySum = 0L;

            totalCopay += copaySum;

            if (status == MatchStatus.COMPLETED) {
                usedAmount = amountSum;
                confirmedCopay = copaySum;
            } else if (status == MatchStatus.CONFIRMED) {
                expectedAmount = amountSum;
            }
        }

        return new VoucherUsageCost(usedAmount, expectedAmount, confirmedCopay, totalCopay);
    }

}
