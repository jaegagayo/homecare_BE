package jaega.homecare.domain.consumer.service.command;

import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.consumer.mapper.ConsumerMapper;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerCommandService {

    private final UserRepository userRepository;
    private final ConsumerMapper consumerMapper;
    private final PasswordEncoder passwordEncoder;

    public void createUser(ConsumerCreateRequest request, UserRole role){
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String password = passwordEncoder.encode(request.password());

        User user = consumerMapper.toEntity(request, password);
        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());
        userRepository.save(user);
    }
}
