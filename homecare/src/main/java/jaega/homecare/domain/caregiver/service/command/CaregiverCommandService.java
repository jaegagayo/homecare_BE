package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.center.dto.req.CreateCaregiverProfileRequest;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.users.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CaregiverCommandService {

    private final CaregiverMapper caregiverMapper;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final CertificationRepository certificationRepository;

    public void createCaregiver(CreateCaregiverRequest createCaregiverRequest, User user, Center center) {
        String address = createCaregiverRequest.address();
        Caregiver caregiver = caregiverMapper.toEntity(address, user, center);
        caregiver.initializeCaregiver(UUID.randomUUID());

        // 자격증 기본 정보 없이 생성
        Certification certification = Certification.builder()
                .certificationId(UUID.randomUUID())
                .caregiver(caregiver)
                .certificationNumber(null)
                .certificationDate(null)
                .trainStatus(false)
                .build();

        certificationRepository.save(certification);

        caregiverRepository.save(caregiver);
    }

    public void createCaregiverProfile(CreateCaregiverProfileRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        caregiver.setCaregiverProfile(request);

        caregiverRepository.save(caregiver);
    }
}
