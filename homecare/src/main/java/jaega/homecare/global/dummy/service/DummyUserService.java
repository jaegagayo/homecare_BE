package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.users.entity.Gender;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyUserService {

    private final UserRepository userRepository;
    private final Random random = new Random();

    protected void generateDummyUsers() {
        IntStream.range(0, 87).forEach(this::createDummyUser);
    }

    private void createDummyUser(int index) {
        // 87명의 이름 리스트
        List<String> koreanNames = List.of(
                "김기현", "박지성", "이재민", "최유진", "장서연",
                "정하늘", "김소희", "이수환", "박민재", "윤지영",
                "강채영", "조민아", "서지수", "신재혁", "배유림",
                "노지민", "황서현", "문지호", "임지훈", "정예원",
                "김도윤", "박서준", "이하늘", "최유나", "장민석",

                "정다은", "김수현", "이준호", "박서연", "윤재민",
                "강민지", "조윤호", "서예린", "신하준", "배지우",
                "노하은", "황민재", "문예준", "임서진", "정시윤",
                "김유진", "박지훈", "이서현", "최준서", "장서윤",
                "정하윤", "김민성", "이하윤", "박민성", "윤서진",

                "강예진", "조민재", "서지훈", "신유나", "배준혁",
                "노예림", "황지호", "문채원", "임준호", "정서연",
                "김지우", "박예준", "이수민", "최지후", "장예린",
                "정민재", "김서연", "이유진", "박하준", "윤채원",
                "강서윤", "조하늘", "서민재", "신서윤", "배하은",

                "노지호", "황서진", "문하윤", "임채원", "정민지",
                "김지훈", "박서윤", "이예준", "최서연", "장하준",
                "정유나", "김채원"
        );

        // 이름에 맞는 정확한 성별 배열
        Gender[] genders = {
                Gender.MALE, Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.FEMALE,
                Gender.FEMALE, Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.FEMALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.FEMALE,
                Gender.MALE, Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.MALE,

                Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.MALE,
                Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.MALE,

                Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.MALE,
                Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.MALE, Gender.FEMALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.FEMALE, Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.FEMALE,

                Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.FEMALE, Gender.FEMALE,
                Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE,
                Gender.FEMALE, Gender.FEMALE
        };

        String name = koreanNames.get(index % koreanNames.size());
        Gender gender = genders[index % genders.length];

        // 이메일 생성
        String email = "user" + index + "@dummy.com";

        UserRole role;
        if (index == 0) {
            role = UserRole.ROLE_CENTER; // 0번은 센터
        } else {
            role = (index % 2 == 0) ? UserRole.ROLE_CAREGIVER : UserRole.ROLE_CONSUMER;
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password("$2a$10$vvUzhakZH7BQ0fpo8RfS/u3Ip54VLNHAQSoBCnCIYKSxVBmAhxaVG")
                .phone("010-1234-" + String.format("%04d", index))
                .birthDate(LocalDate.of(1970 + random.nextInt(30), random.nextInt(12) + 1, random.nextInt(28) + 1))
                .gender(gender)
                .build();

        user.setUser(UUID.randomUUID(), role, LocalDateTime.now());
        userRepository.save(user);
    }
}
