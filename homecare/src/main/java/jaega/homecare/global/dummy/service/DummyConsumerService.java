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

    private static final String[] DUMMY_ADDRESSES = {
            "전라남도 순천시 성동3길 8", "전라남도 순천시 성동3길 2", "전라남도 순천시 조례동 12",
            "전라남도 순천시 연향동 1346", "전라남도 순천시 조례동 1800", "전라남도 순천시 왕지동 23-1",
            "전라남도 순천시 조례동 1659", "전라남도 순천시 봉화1길 146", "전라남도 순천시 성동2길 42",
            "전라남도 순천시 성동2길 46", "전라남도 순천시 조례동 550", "전라남도 순천시 풍덕동 807-7",
            "전라남도 순천시 풍덕동 255-3", "전라남도 순천시 연향동 1659", "전라남도 순천시 매곡동 12-1",

            "전라남도 순천시 매곡동 407", "전남 순천시 석현동 7", "전남 순천시 매곡동 407",
            "전남 순천시 가곡동 429", "전남 순천시 석현동 235", "전남 순천시 조례동 1820",
            "전남 순천시 조례동 68-19", "전남 순천시 조례동 1804", "전남 순천시 조례동 1802",
            "전남 순천시 조례동 1885", "전남 순천시 조례동 550", "전남 순천시 조례동 840",
            "전남 순천시 조례동 1519-12", "전남 순천시 조례동 1450", "전남 순천시 조례동 1421",

            "전남 순천시 조례동 1882", "전남 순천시 조례동 1348", "전남 순천시 조례동 1299-5",
            "전남 순천시 생목동 59", "전남 순천시 조곡동 68-61", "전남 순천시 오천동 951",
            "전남 순천시 오천동 953-1", "전남 순천시 오천동 982", "전남 순천시 연향동 1378",
            "전남 순천시 연향동 1384-3", "전남 순천시 연향동 1386", "전남 순천시 연향동 1387",
            "전남 순천시 연향동 1515", "전남 순천시 해룡면 상삼리 696", "전남 순천시 해룡면 상삼리 691"
    };
}