package jaega.homecare.domain.users.service.command;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.mapper.UserMapper;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void createConsumer(UserCreateRequest request){
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = userMapper.toEntity(request);
        user.setUser(UUID.randomUUID(), UserRole.ROLE_CAREGIVER, LocalDateTime.now());
        userRepository.save(user);
    }
}
