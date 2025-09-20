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

    public void generateDummyUsers() {
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

    private static final double[] DUMMY_LATITUDES = {
            34.9485, 34.9492, 34.9501, 34.9510, 34.9518, 34.9523, 34.9531, 34.9539, 34.9544, 34.9550,
            34.9556, 34.9561, 34.9567, 34.9572, 34.9578, 34.9583, 34.9589, 34.9594, 34.9600, 34.9605,
            34.9611, 34.9616, 34.9622, 34.9627, 34.9633, 34.9638, 34.9644, 34.9649, 34.9655, 34.9660,
            34.9666, 34.9671, 34.9677, 34.9682, 34.9688, 34.9693, 34.9699, 34.9704, 34.9710, 34.9715,
            34.9721, 34.9726, 34.9732
    };

    private static final double[] DUMMY_LONGITUDES = {
            127.4662, 127.4670, 127.4675, 127.4682, 127.4687, 127.4693, 127.4698, 127.4704, 127.4709, 127.4715,
            127.4720, 127.4726, 127.4731, 127.4737, 127.4742, 127.4748, 127.4753, 127.4759, 127.4764, 127.4770,
            127.4775, 127.4781, 127.4786, 127.4792, 127.4797, 127.4803, 127.4808, 127.4814, 127.4819, 127.4825,
            127.4830, 127.4836, 127.4841, 127.4847, 127.4852, 127.4858, 127.4863, 127.4869, 127.4874, 127.4880,
            127.4885, 127.4891, 127.4896
    };

    private static final String[] DUMMY_INTRODUCTIONS = {
            "안녕하세요! 따뜻한 마음과 세심한 관찰로 어르신의 안전을 최우선으로 케어하겠습니다.",
            "안녕하세요! 밝은 미소와 긍정적인 에너지로 어르신과 소통하며 행복한 시간을 만들겠습니다.",
            "안녕하세요! 20년 경력으로 안전하고 전문적인 돌봄을 제공합니다.",
            "안녕하세요! 음악과 미술 활동을 통해 즐거운 일상을 만들어 드리겠습니다.",
            "안녕하세요! 어르신의 건강 관리와 일상 생활 지원에 정성을 다하겠습니다.",
            "안녕하세요! 산책과 운동을 함께하며 신체 건강과 활력을 챙기겠습니다.",
            "안녕하세요! 가족처럼 따뜻하게 보살피며 신뢰받는 돌봄을 제공합니다.",
            "안녕하세요! 약 챙기기와 식사 관리 등 세심한 케어를 약속드립니다.",
            "안녕하세요! 어르신의 기분과 컨디션을 세심하게 관찰하며 돌봄하겠습니다.",
            "안녕하세요! 즐겁고 안전한 환경에서 생활할 수 있도록 최선을 다하겠습니다.",
            "안녕하세요! 밝고 친절한 태도로 어르신과 소통하며 편안함을 제공합니다.",
            "안녕하세요! 전문적인 지식과 경험으로 건강과 안전을 지켜 드리겠습니다.",
            "안녕하세요! 정성과 책임감으로 어르신의 하루를 행복하게 만들겠습니다.",
            "안녕하세요! 웃음과 즐거움을 나누며 편안한 일상을 지원하겠습니다.",
            "안녕하세요! 개인 맞춤형 케어로 어르신 만족도를 높이겠습니다.",
            "안녕하세요! 어르신의 취향과 습관을 존중하며 신뢰받는 돌봄을 제공합니다.",
            "안녕하세요! 세심한 배려와 전문성으로 안전한 환경을 조성하겠습니다.",
            "안녕하세요! 따뜻한 손길과 밝은 미소로 하루를 즐겁게 만들어 드리겠습니다.",
            "안녕하세요! 어르신과 가족 모두가 안심할 수 있는 돌봄을 제공합니다.",
            "안녕하세요! 건강한 생활 습관을 유도하며 안전한 활동을 지원하겠습니다.",
            "안녕하세요! 즐거운 대화와 관심으로 어르신의 마음을 케어하겠습니다.",
            "안녕하세요! 전문적이고 성실한 돌봄으로 신뢰를 쌓겠습니다.",
            "안녕하세요! 어르신의 행복과 안전을 동시에 고려하는 케어를 제공합니다.",
            "안녕하세요! 취미 활동과 여가를 함께 즐기며 삶의 질을 높이겠습니다.",
            "안녕하세요! 밝은 에너지와 친절함으로 즐거운 환경을 만들어 드리겠습니다.",
            "안녕하세요! 정성 어린 돌봄과 책임감 있는 케어로 만족을 드리겠습니다.",
            "안녕하세요! 어르신의 건강과 안전을 위해 세심하게 관찰하겠습니다.",
            "안녕하세요! 즐거움과 편안함을 동시에 제공하는 맞춤형 케어를 제공합니다.",
            "안녕하세요! 따뜻하고 신뢰할 수 있는 손길로 하루를 지켜 드리겠습니다.",
            "안녕하세요! 안전하고 즐거운 활동으로 건강을 챙기며 케어하겠습니다.",
            "안녕하세요! 어르신의 일상에 활기와 행복을 더하는 돌봄을 제공합니다.",
            "안녕하세요! 친절과 배려로 어르신이 안심하고 생활할 수 있도록 하겠습니다.",
            "안녕하세요! 웃음과 관심으로 하루를 행복하게 만드는 케어를 제공합니다.",
            "안녕하세요! 전문성과 경험을 바탕으로 안전하고 신뢰받는 돌봄을 제공합니다.",
            "안녕하세요! 어르신과 가족 모두가 만족할 수 있는 맞춤형 케어를 약속드립니다.",
            "안녕하세요! 신체 활동과 사회적 교류를 함께하며 즐거움을 나누겠습니다.",
            "안녕하세요! 따뜻한 마음과 세심한 배려로 하루를 안전하게 지켜 드리겠습니다.",
            "안녕하세요! 즐거움과 안전을 동시에 고려한 케어로 행복을 만들어 드리겠습니다.",
            "안녕하세요! 어르신의 편안함과 만족도를 최우선으로 생각하며 돌봄합니다.",
            "안녕하세요! 긍정적인 에너지와 책임감으로 안정적인 돌봄을 제공합니다.",
            "안녕하세요! 세심한 관찰과 배려로 어르신의 생활을 지원하겠습니다.",
            "안녕하세요! 즐거운 마음과 친절함으로 하루를 안전하게 케어하겠습니다.",
            "안녕하세요! 전문적이고 따뜻한 손길로 신뢰받는 돌봄을 약속드립니다.",
            "안녕하세요! 어르신의 건강과 행복을 동시에 지키며 케어하겠습니다."
    };

    // 예시: 순천 외 주소 선택 함수
    private String pickNonSuncheonAddress(int index) {
        String[] otherAddresses = {
                "경기도 수원시 팔달구 매산로 1",
                "전라북도 전주시 완산구 전동 3",
                "충청북도 청주시 상당구 사직로 5",
                "충청남도 천안시 동남구 중앙로 12",
                "경상북도 포항시 남구 대이로 45",
                "경상남도 창원시 성산구 중앙대로 34"
        };
        return otherAddresses[index % otherAddresses.length];
    }
}
