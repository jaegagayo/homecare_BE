package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.mapper.CertificationMapper;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificationQueryService {
    private final CertificationRepository certificationRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final CertificationMapper certificationMapper;

    public Certification getCertification(UUID certificationId){
        return certificationRepository.findByCertificationId(certificationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 certificationId로 자격증을 찾을 수 없습니다."));
    }

    public Certification findCertificationByCaregiver(Caregiver caregiver){
        return certificationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new EntityNotFoundException("해당 Caregiver로 자격증을 찾을 수 없습니다."));
    }

    public GetCertificationResponse getCertificationByCaregiver(UUID caregiverId){
        Caregiver caregiver = caregiverQueryService.getCaregiver(caregiverId);
        Certification certification = findCertificationByCaregiver(caregiver);
        return certificationMapper.toGetResponse(certification);
    }
}
