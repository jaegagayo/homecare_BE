package jaega.homecare.domain.consumer.service.command;

import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerProfileUpdateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.consumer.dto.res.ConsumerLoginResponse;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.mapper.ConsumerMapper;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jaega.homecare.domain.voucher.service.command.VoucherCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerCommandService {

    private final ConsumerRepository consumerRepository;
    private final ConsumerMapper consumerMapper;
    private final UserCommandService userCommandService;
    private final VoucherCommandService voucherCommandService;

    public void signupConsumer(ConsumerSignupRequest request){
        User user = userCommandService.createUser(request.user(), UserRole.ROLE_CONSUMER);
        createConsumer(request.consumer(), user);

    }

    public void createConsumer(ConsumerCreateRequest request, User user){
        Consumer consumer = consumerMapper.toConsumer(request, user);
        consumer.initializeConsumer(UUID.randomUUID());
        consumerRepository.save(consumer);

        // 수요자의 현재 월의 바우처 생성
        voucherCommandService.createVoucher(consumer.getConsumerId());
    }

    public void updateConsumer(ConsumerProfileUpdateRequest request, Consumer consumer){
        userCommandService.updateUser(request.userRequest(), consumer.getUser());
        consumer.updateConsumer(request.consumerRequest());

        consumerRepository.save(consumer);
    }

    public ConsumerLoginResponse loginConsumer(UserLoginRequest request){
        User user = userCommandService.loginUser(request);
        Consumer consumer = consumerRepository.findByUser(user);
        if(consumer == null) throw new BadCredentialsException("로그인에 실패했습니다.");
        return consumerMapper.toLoginResponse(consumer);
    }
}
