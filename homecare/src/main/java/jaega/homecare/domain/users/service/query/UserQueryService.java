package jaega.homecare.domain.users.service.query;

import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;

    public User getUser(UUID userId){
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
    }
}
