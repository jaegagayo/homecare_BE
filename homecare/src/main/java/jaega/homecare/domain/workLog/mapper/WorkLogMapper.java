package jaega.homecare.domain.workLog.mapper;

import jaega.homecare.domain.workLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.workLog.dto.res.GetWorkLogResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.workLog.entity.WorkLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

    @Mapping(target = "workStartTime", source = "request.workStartTime")
    @Mapping(target = "workEndTime", source = "request.workEndTime")
    @Mapping(target = "workDate", source = "request.workDate")
    @Mapping(target = "workAddress", source = "request.workAddress")
    @Mapping(target = "distanceLog", source = "request.distanceLog")
    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "settlementAmount", source = "settlementAmount")
    @Mapping(target = "isPaid", ignore = true)
    @Mapping(target = "workLogId", ignore = true)
    WorkLog toEntity(CreateWorkLogRequest request, Caregiver caregiver, BigDecimal settlementAmount);

    @Mapping(target = "workLogId", source = "workLogId")
    @Mapping(target = "workStartTime", source = "workStartTime")
    @Mapping(target = "workEndTime", source = "workEndTime")
    @Mapping(target = "distanceLog", source = "distanceLog")
    @Mapping(target = "isPaid", source = "paid")
    GetWorkLogResponse toGetResponse(WorkLog workLog);
}