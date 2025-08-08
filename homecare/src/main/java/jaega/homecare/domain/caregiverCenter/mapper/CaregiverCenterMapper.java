package jaega.homecare.domain.caregiverCenter.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.center.entity.Center;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaregiverCenterMapper {

    @Mapping(target = "caregiverCenterId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    CaregiverCenter toEntity(Caregiver caregiver, Center center);
}
