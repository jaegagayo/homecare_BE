package jaega.homecare.domain.voucher.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.voucher.entity.QVoucher;
import jaega.homecare.domain.voucher.entity.Voucher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static jaega.homecare.domain.voucher.entity.QVoucher.voucher;

@Repository
@RequiredArgsConstructor
public class VoucherQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * (논의 필요)
     * 월 별 무조건 하나의 바우처만 저장된다 가정하고 작성
     **/
    public UUID findByConsumerId(UUID consumerId) {
        QVoucher voucher = QVoucher.voucher;

        return queryFactory
                .select(voucher.voucherId)
                .from(voucher)
                .where(
                        voucher.consumer.consumerId.eq(consumerId),
                        voucher.voucherDate.year().eq(LocalDate.now().getYear()),
                        voucher.voucherDate.month().eq(LocalDate.now().getMonthValue())
                )
                .fetchOne();
    }

    public Voucher findByConsumerIdAndMonth(UUID consumerId, YearMonth targetMonth) {
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        return queryFactory
                .selectFrom(voucher)
                .where(
                        voucher.consumer.consumerId.eq(consumerId), // Consumer ID 기준
                        voucher.voucherDate.between(startDate, endDate) // 월 기준
                )
                .fetchOne();

    }
}
