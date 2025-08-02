package jaega.homecare.domain.caregiver.mapper;

import jaega.homecare.domain.caregiver.dto.req.CreateCertificationRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "certificationId", ignore = true)
    Certification toEntity(CreateCertificationRequest request, Caregiver caregiver);

    GetCertificationResponse toGetResponse(Certification certification);
}
