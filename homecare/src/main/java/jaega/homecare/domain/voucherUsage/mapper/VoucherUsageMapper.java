package jaega.homecare.domain.voucherUsage.mapper;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VoucherUsageMapper {


    @Mapping(target = "voucher", source = "voucher")
    @Mapping(target = "serviceMatch", source = "serviceMatch")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "copay", source = "copay")
    @Mapping(target = "voucherUsageId", ignore = true)
    VoucherUsage toEntity(Voucher voucher, ServiceMatch serviceMatch, long amount, long copay);

    @Mapping(target = "isHighCopayRate",
            expression = "java(isHighCopayRate(remainingAmount, expectedUsageAmount))")
    VoucherUsageGuideResponse toVoucherUsageGuideResponse(Long remainingAmount, Long expectedUsageAmount, Long expectedCopay);

    @Named("isHighCopayRate")
    default boolean isHighCopayRate(Long remainingAmount, Long expectedUsageAmount) {
        return remainingAmount < expectedUsageAmount;
    }

}
