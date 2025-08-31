package jaega.homecare.domain.voucher.service.command;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.mapper.VoucherMapper;
import jaega.homecare.domain.voucher.repository.VoucherRepository;
import jaega.homecare.domain.voucher.service.query.VoucherQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherCommandService {
    private final VoucherRepository voucherRepository;
    private final ConsumerQueryService consumerQueryService;
    private final VoucherMapper voucherMapper;

    public void createVoucher(UUID consumerId){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        Long totalAmount = getTotalAmountByCareGrade(consumer.getCareGrade());
        Voucher voucher = voucherMapper.toEntity(consumer, LocalDate.now(), totalAmount);
        voucher.initializeVoucher(UUID.randomUUID());
        voucherRepository.save(voucher);
    }

    public long getTotalAmountByCareGrade(int careGrade) {
        return switch (careGrade) {
            case 1 -> 1_520_700L;
            case 2 -> 1_351_700L;
            case 3 -> 1_295_400L;
            case 4 -> 1_189_800L;
            case 5 -> 1_021_300L;
            default -> 573_900L; // 인지지원등급
        };
    }
}
