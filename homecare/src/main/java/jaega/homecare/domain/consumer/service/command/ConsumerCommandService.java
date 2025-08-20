package jaega.homecare.domain.consumer.service.command;

import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.mapper.ConsumerMapper;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerCommandService {

    private final ConsumerRepository consumerRepository;
    private final ConsumerMapper consumerMapper;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public void signupConsumer(ConsumerSignupRequest request){
        UUID userId = userCommandService.createUser(request.user(), UserRole.ROLE_CONSUMER);
        createConsumer(request.consumer(), userId);
    }

    public void createConsumer(ConsumerCreateRequest request, UUID userId){
        User user = userQueryService.getUser(userId);
        Consumer consumer = consumerMapper.toConsumer(request, user);
        consumer.setConsumer(UUID.randomUUID());
        consumerRepository.save(consumer);
    }
}
