package jaega.homecare.domain.consumer.mapper;

import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.consumer.dto.res.ConsumerDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerLoginResponse;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsumerMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "residentialAddress", source = "request.residentialAddress")
    @Mapping(target = "visitAddress", source = "request.visitAddress")
    @Mapping(target = "entranceType", source = "request.entranceType")
    @Mapping(target = "careGrade", source = "request.careGrade")
    @Mapping(target = "isMedicalAid", source = "request.isMedicalAid")
    @Mapping(target = "disease", source = "request.disease")
    @Mapping(target = "cognitiveStatus", source = "request.cognitiveStatus")
    @Mapping(target = "livingSituation", source = "request.livingSituation")
    @Mapping(target = "guardianName", source = "request.guardianName")
    @Mapping(target = "guardianPhone", source = "request.guardianPhone")
    @Mapping(target = "consumerId", ignore = true)
    Consumer toConsumer(ConsumerCreateRequest request, User user);

    ConsumerLoginResponse toLoginResponse(Consumer consumer);

    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "birthDate", source = "user.birthDate")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "phone", source = "user.phone")
    ConsumerDetailResponse toDetailResponse(Consumer consumer);

}
