package jaega.homecare.domain.users.service.command;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.dto.req.UserUpdateRequest;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.mapper.UserMapper;
import jaega.homecare.domain.users.repository.UserRepository;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public User createUser(UserCreateRequest request, UserRole role){
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String password = passwordEncoder.encode(request.password());

        User user = userMapper.toEntity(request, password);
        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());
        userRepository.save(user);

        return user;
    }

    public void updateUser(UserUpdateRequest request, User user){
        user.updateUser(request);
        userRepository.save(user);
    }
}
