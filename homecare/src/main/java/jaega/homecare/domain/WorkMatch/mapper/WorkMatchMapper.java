package jaega.homecare.domain.WorkMatch.mapper;

import jaega.homecare.domain.WorkLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface WorkMatchMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "workingDate", source = "workingDate")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "workMatchId", ignore = true)
    @Mapping(target = "status", ignore = true)
    WorkMatch toEntity(Caregiver caregiver, LocalDate workingDate, LocalTime startTime, LocalTime endTime);

}
