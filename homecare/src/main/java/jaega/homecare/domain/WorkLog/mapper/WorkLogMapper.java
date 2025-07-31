package jaega.homecare.domain.WorkLog.mapper;

import jaega.homecare.domain.WorkLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

    @Mapping(target = "workMatch", source = "request.workMatch")
    @Mapping(target = "workTime_start", source = "request.workTime_start")
    @Mapping(target = "workTime_end", source = "request.workTime_end")
    @Mapping(target = "distanceLog", source = "request.distanceLog")
    @Mapping(target = "workLogId", ignore = true)
    @Mapping(target = "isPaid", ignore = true)
    WorkLog toEntity(CreateWorkLogRequest request);
}
