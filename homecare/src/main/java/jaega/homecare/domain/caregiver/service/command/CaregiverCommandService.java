package jaega.homecare.domain.caregiver.service.command;

import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.CaregiverCreateRequest;
import jaega.homecare.domain.caregiver.dto.res.CaregiverLoginResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverSignupResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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

    public GetCaregiverSignupResponse signupCaregiver(CaregiverSignupRequest request){
        User user = userCommandService.createUser(request.user(), UserRole.ROLE_CAREGIVER);
        Caregiver caregiver = createCaregiver(request.caregiver(), user);
        certificationCommandService.createCertification(request.certification(), caregiver);
        return caregiverMapper.toGetCaregiverSignup(caregiver);
    }

    public Caregiver createCaregiver(CaregiverCreateRequest request, User user) {
        Caregiver caregiver = caregiverMapper.toEntity(request, user);
        caregiver.initializeCaregiver(UUID.randomUUID());

        return caregiverRepository.save(caregiver);
    }

    public CaregiverLoginResponse loginCaregiver(UserLoginRequest request){
        User user = userCommandService.loginUser(request);
        Caregiver caregiver = caregiverRepository.findByUser(user);
        if(caregiver == null) throw new BadCredentialsException("로그인에 실패했습니다.");
        return caregiverMapper.toLoginResponse(caregiver);
    }

}
