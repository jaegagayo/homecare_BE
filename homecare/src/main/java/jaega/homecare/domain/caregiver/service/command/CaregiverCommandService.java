package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.CaregiverCreateRequest;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.UserCommandService;
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
    private final CertificationCommandService certificationCommandService;
    private final UserCommandService userCommandService;

    public void signupCaregiver(CaregiverSignupRequest request){
        User user = userCommandService.createUser(request.user(), UserRole.ROLE_CAREGIVER);
        Caregiver caregiver = createCaregiver(request.caregiver(), user);
        certificationCommandService.createCertification(request.certification(), caregiver);
    }

    public Caregiver createCaregiver(CaregiverCreateRequest request, User user) {
        Caregiver caregiver = caregiverMapper.toEntity(request, user);
        caregiver.initializeCaregiver(UUID.randomUUID());

        return caregiverRepository.save(caregiver);
    }

}
