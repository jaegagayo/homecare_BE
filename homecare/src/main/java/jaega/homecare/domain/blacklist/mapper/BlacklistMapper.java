package jaega.homecare.domain.blacklist.mapper;

import jaega.homecare.domain.blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.blacklist.dto.res.GetBlacklistByConsumerResponse;
import jaega.homecare.domain.blacklist.entity.Blacklist;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "consumer", source = "consumer")
    @Mapping(target = "blacklistId", ignore = true)
    Blacklist toEntity(CreateBlacklistByConsumerRequest request, Caregiver caregiver, Consumer consumer);

    @Mapping(target = "blacklistId", source = "blacklistId")
    @Mapping(target = "caregiverId", source = "caregiver.caregiverId")
    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "consumerId", source = "consumer.consumerId")
    @Mapping(target = "consumerName", source = "consumer.user.name")
    GetBlacklistByConsumerResponse toGetResponse(Blacklist blacklist);
}