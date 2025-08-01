package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
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

    public void createCaregiver(CreateCaregiverRequest createCaregiverRequest, User user, Center center) {
        String address = createCaregiverRequest.address();
        Caregiver caregiver = caregiverMapper.toEntity(address, user, center);
        caregiver.initializeCaregiver(UUID.randomUUID());

        caregiverRepository.save(caregiver);
    }

    public void createCaregiverProfile(CreateCaregiverProfileRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        caregiver.setCaregiverProfile(request);

        caregiverRepository.save(caregiver);
    }
}
