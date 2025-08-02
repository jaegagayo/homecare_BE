package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.dto.req.CreateCertificationRequest;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.mapper.CertificationMapper;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiver.service.query.CertificationQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationCommandService {

    private final CertificationRepository certificationRepository;
    private final CertificationMapper certificationMapper;
    private final CaregiverQueryService caregiverQueryService;
    private final CertificationQueryService certificationQueryService;

    public void createCertification(CreateCertificationRequest request) {
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        // 자격증이 이미 있는지 확인 (caregiver 기준으로 조회한다고 가정)
        Optional<Certification> optionalCertification = certificationRepository.findByCaregiver(caregiver);

        if (optionalCertification.isPresent()) {
            // 이미 자격증이 있으면 수정
            Certification certification = optionalCertification.get();
            certification.update(
                    request.certificationNumber(),
                    request.certificationDate()
            );
        } else {
            // 없으면 신규 등록
            Certification certification = certificationMapper.toEntity(request, caregiver);
            certification.setCertification(UUID.randomUUID());
            certificationRepository.save(certification);
        }
    }

    public void changeTrainStatus(UUID certificationId){
        Certification certification = certificationQueryService.getCertification(certificationId);
        certification.changeStatus(certification.isTrainStatus());
        certificationRepository.save(certification);
    }
}
