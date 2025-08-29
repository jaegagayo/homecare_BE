package jaega.homecare.domain.voucher.service.query;

import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.repository.VoucherQueryRepository;
import jaega.homecare.domain.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;
    private final VoucherQueryRepository voucherQueryRepository;

    // 바우처 도메인 조회
    public Voucher getVoucher(UUID voucherId){
        return voucherRepository.findByVoucherId(voucherId)
                .orElseThrow(() -> new NoSuchElementException("해당 바우처 ID로 바우처를 찾을 수 없습니다."));
    }

    public Long getTotalAmount(UUID voucherId) {
        return voucherRepository.findByVoucherId(voucherId)
                .orElseThrow(() -> new NoSuchElementException("해당 되는 바우처 ID를 찾을 수 없습니다. "))
                .getTotalAmount();
    }

    // 현 월에 사용되는 바우처를 ConsumerId 기준으로
    public UUID getVoucherIdByConsumerId(UUID consumerId) {
        return voucherQueryRepository.findByConsumerId(consumerId);
    }
}
