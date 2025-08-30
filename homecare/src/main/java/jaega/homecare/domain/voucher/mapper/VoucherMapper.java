package jaega.homecare.domain.voucher.mapper;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.voucher.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    @Mapping(target = "consumer", source = "consumer")
    @Mapping(target = "voucherDate", source = "voucherDate")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "voucherId", ignore = true)
    Voucher toEntity(Consumer consumer, LocalDate voucherDate, Long totalAmount);

}
