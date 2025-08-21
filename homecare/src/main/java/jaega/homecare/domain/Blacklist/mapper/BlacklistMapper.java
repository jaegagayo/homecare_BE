package jaega.homecare.domain.Blacklist.mapper;

import jaega.homecare.domain.Blacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.Blacklist.dto.res.GetCaregiverBlacklistResponse;
import jaega.homecare.domain.Blacklist.entity.Blacklist;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "consumer", source = "consumer")
    @Mapping(target = "blacklistId", ignore = true)
    Blacklist toEntity(CreateCaregiverBlacklistRequest request, Caregiver caregiver, Consumer consumer);

    @Mapping(target = "blacklistId", source = "blacklistId")
    @Mapping(target = "caregiverId", source = "caregiver.caregiverId")
    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "consumerId", source = "consumer.consumerId")
    @Mapping(target = "consumerName", source = "consumer.user.name")
    GetCaregiverBlacklistResponse toGetResponse(Blacklist blacklist);
}