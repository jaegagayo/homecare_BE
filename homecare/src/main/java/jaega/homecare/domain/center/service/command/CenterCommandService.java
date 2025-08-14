package jaega.homecare.domain.center.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.service.command.CaregiverCenterCommandService;
import jaega.homecare.domain.center.dto.req.CenterLoginRequest;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.center.dto.res.CenterLoginResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.mapper.CenterMapper;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterCommandService {

    private final UserRepository userRepository;
    private final CenterMapper centerMapper;
    private final CaregiverMapper caregiverMapper;
    private final PasswordEncoder passwordEncoder;
    private final CenterQueryService centerQueryService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverCenterCommandService caregiverCenterCommandService;

    // 요양보호사 등록
    public void registerCaregiver(CreateCaregiverRequest createCaregiverRequest, UserRole role, UUID centerId) {
        Center center = centerQueryService.findCenterByUUID(centerId);

        if (userRepository.existsByEmail(createCaregiverRequest.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // User 생성
        String rawPassword = generateRandomPassword(8);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = caregiverMapper.toUserEntity(createCaregiverRequest, encodedPassword);
        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());
        userRepository.save(user);

        Caregiver caregiver = caregiverCommandService.createCaregiver(createCaregiverRequest, user, centerId);

        caregiverCenterCommandService.createCaregiverCenter(center, caregiver);
    }

    // 요양보호사 말소
    public void deregisterCaregiver(UUID centerId, UUID caregiverId) {
        CaregiverCenter caregiverCenter = caregiverCenterCommandService.getCaregiverCenterByAllId(centerId, caregiverId);

        caregiverCenter.deregister();
    }

    // 임시 로그인
    public CenterLoginResponse loginCenterWithoutAuth(){
        return new CenterLoginResponse(UUID.fromString("1534ae77-5ded-4764-86ce-d3215968a110"));
    }

    // 실제 로그인
    public CenterLoginResponse loginCenter(CenterLoginRequest request){
        User user = userRepository.findByEmail(request.email());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("로그인에 실패했습니다.");
        }
        Center center = centerQueryService.findCenterByUser(user);

        return centerMapper.toLoginResponse(center);
    }

    // 랜덤 비밀번호 생성
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        return random.ints(length, 0, chars.length())
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());
    }

}
