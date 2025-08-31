package jaega.homecare.domain.voucherUsage.mapper;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageCost;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageDetail;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageResponse;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VoucherUsageMapper {


    @Mapping(target = "voucher", source = "voucher")
    @Mapping(target = "serviceMatch", source = "serviceMatch")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "copay", source = "copay")
    @Mapping(target = "voucherUsageId", ignore = true)
    VoucherUsage toEntity(Voucher voucher, ServiceMatch serviceMatch, long amount, long copay);

    @Mapping(source = "serviceMatch.serviceDate", target = "serviceDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "copay", target = "copay")
    @Mapping(source = "serviceMatch.serviceRequest.serviceType", target = "serviceType")
    @Mapping(source = "serviceMatch.matchStatus", target = "matchStatus")
    VoucherUsageDetail toVoucherUsageDetail(VoucherUsage usage);

    @Mapping(target = "totalAmount", source = "voucher.totalAmount")
    @Mapping(target = "usedAmount", source = "cost.usedAmount")
    @Mapping(target = "expectedAmount", source = "cost.expectedAmount")
    @Mapping(target = "remainingAmount", source = "remainingAmount")
    @Mapping(target = "confirmedCopay", source = "cost.confirmedCopay")
    @Mapping(target = "totalCopay", source = "totalCopay")
    @Mapping(target = "usageList", source = "usageList")
    VoucherUsageResponse toVoucherUsageResponse(Voucher voucher, VoucherUsageCost cost, long remainingAmount, long totalCopay, List<VoucherUsageDetail> usageList);

    @Mapping(target = "isHighCopayRate",
            expression = "java(isHighCopayRate(remainingAmount, expectedUsageAmount))")
    VoucherUsageGuideResponse toVoucherUsageGuideResponse(Long remainingAmount, Long expectedUsageAmount, Long expectedCopay);

    @Named("isHighCopayRate")
    default boolean isHighCopayRate(Long remainingAmount, Long expectedUsageAmount) {
        return remainingAmount < expectedUsageAmount;
    }

}
