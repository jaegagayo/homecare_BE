package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverCommandService {

    private final CaregiverMapper caregiverMapper;
    private final CaregiverRepository caregiverRepository;

    public void createCaregiver(CreateCaregiverRequest createCaregiverRequest, User user, Center center) {
        String address = createCaregiverRequest.address();
        Caregiver caregiver = caregiverMapper.toEntity(address, user, center);
        caregiver.initializeCaregiver(UUID.randomUUID());

        caregiverRepository.save(caregiver);
    }
}
