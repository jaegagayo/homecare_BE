package jaega.homecare.domain.voucherUsage.mapper;

import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VoucherUsageMapper {

    @Mapping(target = "isHighCopayRate",
            expression = "java(isHighCopayRate(remainingAmount, expectedUsageAmount))")
    VoucherUsageGuideResponse toVoucherUsageGuideResponse(Long remainingAmount, Long expectedUsageAmount, Long expectedCopay);

    @Named("isHighCopayRate")
    default boolean isHighCopayRate(Long remainingAmount, Long expectedUsageAmount) {
        return remainingAmount < expectedUsageAmount;
    }

}
