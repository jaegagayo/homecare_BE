package jaega.homecare.domain.voucher.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jaega.homecare.domain.voucher.entity.QVoucher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

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
                .where(
                        voucher.consumer.consumerId.eq(consumerId),
                        voucher.voucherDate.year().eq(LocalDate.now().getYear()),
                        voucher.voucherDate.month().eq(LocalDate.now().getMonthValue())
                )
                .fetchOne();
    }
}
