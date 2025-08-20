package jaega.homecare.domain.workMatch.mapper;

import jaega.homecare.domain.workMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.workMatch.dto.res.GetWorkMatchResponse;
import jaega.homecare.domain.workMatch.entity.WorkMatch;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface WorkMatchMapper {

    @Mapping(target = "startTime", source = "request.workTime_start")
    @Mapping(target = "endTime", source = "request.workTime_end")
    @Mapping(target = "workAddress", source = "request.address")
    @Mapping(target = "distanceLog", source = "request.distanceLog")
    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "workDate", source = "workDate")
    @Mapping(target = "settlementAmount", source = "settlementAmount")
    @Mapping(target = "isPaid", ignore = true)
    @Mapping(target = "workMatchId", ignore = true)
    WorkMatch toEntity(CreateWorkMatchRequest request, Caregiver caregiver, LocalDate workDate, BigDecimal settlementAmount);

    @Mapping(target = "workMatchId", source = "workMatchId")
    @Mapping(target = "workTime_start", source = "startTime")
    @Mapping(target = "workTime_end", source = "endTime")
    @Mapping(target = "distanceLog", source = "distanceLog")
    @Mapping(target = "isPaid", source = "paid")
    GetWorkMatchResponse toGetResponse(WorkMatch workMatch);
}