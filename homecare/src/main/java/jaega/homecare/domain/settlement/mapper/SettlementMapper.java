package jaega.homecare.domain.settlement.mapper;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.dto.res.GetSettlementByCaregiverResponse;
import jaega.homecare.domain.settlement.dto.res.GetSettlementResponse;
import jaega.homecare.domain.settlement.entity.Settlement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SettlementMapper {

    @Mapping(target = "serviceMatch", source = "serviceMatch")
    @Mapping(target = "caregiverCenter", source = "caregiverCenter")
    @Mapping(target = "distanceLog", source = "request.distanceLog")
    @Mapping(target = "settlementAmount", source = "settlementAmount")
    @Mapping(target = "isPaid", ignore = true)
    @Mapping(target = "settlementId", ignore = true)
    Settlement toEntity(CreateSettlementRequest request, ServiceMatch serviceMatch, CaregiverCenter caregiverCenter,
                        BigDecimal settlementAmount);

    @Mapping(source = "caregiverCenter.center.user.name", target = "centerName")
    @Mapping(source = "settlementId", target = "settlementId")
    @Mapping(source = "caregiverCenter.caregiver.user.name", target = "caregiverName")
    @Mapping(source = "settlementAmount", target = "settlementAmount")
    @Mapping(source = "settlement.serviceMatch.serviceDate", target = "serviceDate")
    @Mapping(source = "settlement.serviceMatch.serviceStartTime", target = "serviceStartTime")
    @Mapping(source = "settlement.serviceMatch.serviceEndTime", target = "serviceEndTime")
    @Mapping(source = "paid", target = "isPaid")
    @Mapping(source = "distanceLog", target = "distanceLog")
    GetSettlementByCaregiverResponse toDto(Settlement settlement);

    List<GetSettlementByCaregiverResponse> toDtoList(List<Settlement> settlements);

}
