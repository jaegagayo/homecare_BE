package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import jaega.homecare.domain.users.entity.*;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;

    private final DummyUserService dummyUserService;
    private final DummyCenterService dummyCenterService;
    private final DummyCaregiverService dummyCaregiverService;
    private final DummyConsumerService dummyConsumerService;
    private final DummyServiceRequestService dummyServiceRequestService;

    @Transactional
    public void generateAllDummyData() {
        // 1. 모든 사용자 데이터 먼저 생성
        dummyUserService.generateDummyUsers();

        // 2. Center와 Caregiver는 USER 데이터에 의존하므로, USER 생성 후 실행

        // 더미 센터 생성
        dummyCenterService.generateDummyCenter();

        // 3. 더미 요양보호사 생성
        dummyCaregiverService.generateDummyCaregiverForLogging();

        // 4. Consumer 생성
        dummyConsumerService.generateDummyConsumer();

        // 4-1. dummy1@user.com 전용 Consumer 보장 생성
        User dummyUser = userRepository.findByEmail("user1@dummy.com");
        Consumer dummyConsumer = consumerRepository.findByUser(dummyUser);

        // 전용 ServiceRequest / ServiceMatch / VoucherUsage / Review 생성
        dummyServiceRequestService.createDummyServiceRequestForConsumer(dummyConsumer);

        dummyServiceRequestService.generateDummyServiceRequest();
    }
}
