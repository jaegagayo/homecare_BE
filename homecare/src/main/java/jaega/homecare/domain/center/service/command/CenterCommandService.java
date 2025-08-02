package jaega.homecare.domain.center.service.command;

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

    private final CenterQueryService centerQueryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CenterMapper centerMapper;
    private final CaregiverMapper caregiverMapper;

    // 유저 생성
    // (이 부분이 기현 님이 작성하신 메소드와 겹치는 부분입니다.)
    public User createUser(CreateCaregiverRequest createCaregiverRequest, UserRole role) {
        if (userRepository.existsByEmail(createCaregiverRequest.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String rawPassword = generateRandomPassword(8);
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = caregiverMapper.toUserEntity(createCaregiverRequest, encodedPassword);
        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());

        return userRepository.save(user);
    }

    public CenterLoginResponse loginCenter(CenterLoginRequest request){
        /*
        User user = userRepository.findByEmail(request.email());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("로그인에 실패했습니다.");
        }
        Center center = centerQueryService.getCenterByUser(user);

         */

       // return centerMapper.toLoginResponse(center);


        return new CenterLoginResponse(UUID.fromString("1534ae77-5ded-4764-86ce-d3215968a110"));
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
