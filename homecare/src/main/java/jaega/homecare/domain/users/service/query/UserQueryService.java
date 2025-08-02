package jaega.homecare.domain.users.service.query;

import jaega.homecare.domain.users.dto.res.GetConsumerResponse;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.mapper.UserMapper;
import jaega.homecare.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUser(UUID userId){
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    public GetConsumerResponse findConsumer(UUID userId){
        User user = getUser(userId);
        return userMapper.toGetConsumerResponse(user);
    }
}
