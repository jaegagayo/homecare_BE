package jaega.homecare.domain.voucherUsage.service.command;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucherUsage.entity.ServiceFee;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import jaega.homecare.domain.voucherUsage.mapper.VoucherUsageMapper;
import jaega.homecare.domain.voucherUsage.repository.VoucherUsageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherUsageCommandService {
    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherUsageMapper voucherUsageMapper;

    // 방문 요양 기준으로만 금액 책정
    public void createVoucherUsage(Voucher voucher, ServiceMatch serviceMatch){
        int durationMinutes = (int) Duration.between(
                serviceMatch.getServiceStartTime(),
                serviceMatch.getServiceEndTime()
        ).toMinutes();

        ServiceFee fee = ServiceFeeTable.getFee(durationMinutes);

        VoucherUsage voucherUsage = voucherUsageMapper.toEntity(voucher, serviceMatch, fee.totalAmount(), fee.copay());
        voucherUsage.initializeVoucherUsage(UUID.randomUUID());
        voucherUsageRepository.save(voucherUsage);
    }
}
