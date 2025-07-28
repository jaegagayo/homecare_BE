package jaega.homecare.domain.users.service.command;

import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.mapper.UserMapper;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserLoginResponse userLogin(UserLoginRequest request){
        User user = userRepository.findByEmail(request.email());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("로그인에 실패했습니다.");
        }

        return userMapper.toLoginResponse(user.getUserId());
    }
}
