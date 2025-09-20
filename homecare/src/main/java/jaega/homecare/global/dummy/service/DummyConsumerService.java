package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.consumer.entity.CognitiveStatus;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.repository.UserRepository;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.repository.VoucherRepository;
import jaega.homecare.domain.voucher.service.command.VoucherCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static jaega.homecare.global.dummy.service.DummyData.DUMMY_ADDRESSES;

@Service
@RequiredArgsConstructor
public class DummyConsumerService {

    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;
    private final VoucherCommandService voucherCommandService;
    private final VoucherRepository voucherRepository;
    private final Random random = new Random();

    private final DummyRecurringOfferService dummyRecurringOfferService;

    public void generateDummyConsumer(){
        List<User> consumers = userRepository.findByUserRole(UserRole.ROLE_CONSUMER);
        IntStream.range(0, consumers.size()).forEach(index -> createDummyConsumer(index, consumers));
    }

    private void createDummyConsumer(int index, List<User> consumers) {
        User user = consumers.get(index);

        Consumer consumer = Consumer.builder()
                .user(user)
                .consumerId(UUID.randomUUID())
                .residentialAddress(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
                .visitAddress(DUMMY_ADDRESSES[index % DUMMY_ADDRESSES.length])
                .entranceType("공동현관 비밀번호: " + (1000 + random.nextInt(9000)))
                .careGrade(random.nextInt(6) + 1)
                .isMedicalAid(random.nextBoolean())
                .weight(40 + random.nextInt(40)) // 40~80kg
                .disease(Disease.values()[random.nextInt(Disease.values().length)])
                .cognitiveStatus(CognitiveStatus.values()[random.nextInt(CognitiveStatus.values().length)])
                .livingSituation("혼자 거주")
                .guardianName("보호자" + index)
                .guardianPhone("010-9999-" + String.format("%04d", index))
                .build();

        consumer.initializeConsumer(UUID.randomUUID());
        consumerRepository.save(consumer);

        // ✅ Consumer 생성 시 Voucher 생성
        Long totalAmount = voucherCommandService.getTotalAmountByCareGrade(consumer.getCareGrade());

        Voucher voucher = Voucher.builder()
                .voucherId(UUID.randomUUID())
                .consumer(consumer)
                .voucherDate(LocalDate.now()) // 이번 달 바우처
                .totalAmount(totalAmount)
                .build();

        voucherRepository.save(voucher);

        // 첫 번째 Consumer → 무조건 첫 번째 Caregiver로 정기 제안 생성
        boolean useFirstCaregiver = (index == 0);
        dummyRecurringOfferService.createDummyRecurringOfferForConsumer(consumer, useFirstCaregiver);
    }
}