package jaega.homecare.domain.settlement.mapper;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.dto.res.GetSettlementResponse;
import jaega.homecare.domain.settlement.entity.Settlement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

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

    @Mapping(target = "settlementId", source = "settlementId")
    @Mapping(target = "serviceStartTime", source = "serviceStartTime")
    @Mapping(target = "serviceEndTime", source = "serviceEndTime")
    @Mapping(target = "distanceLog", source = "distanceLog")
    @Mapping(target = "isPaid", source = "paid")
    GetSettlementResponse toGetResponse(Settlement settlement);
}
