package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.Certification;
import jaega.homecare.domain.caregiver.entity.KoreanProficiency;
import jaega.homecare.domain.caregiver.entity.VerifiedStatus;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.repository.CertificationRepository;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.entity.PreferredGender;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.repository.CenterRepository;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.users.entity.*;
import jaega.homecare.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DummyCaregiverService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final CertificationRepository certificationRepository;
    private final Random random = new Random();

    public void generateDummyCaregiver(){
        List<User> caregivers = userRepository.findByUserRole(UserRole.ROLE_CAREGIVER);
        createDummyCaregiversWithConditions(caregivers);
    }

    // 전시용 로그 및 데이터 생성 로직
    private void createDummyCaregiversWithConditions(List<User> users) {
        Center center = centerRepository.findAll().get(0);

        // 각 조건별 카운트
        List<Caregiver> dayOfWeekFiltered = new ArrayList<>();
        List<Caregiver> timeFiltered = new ArrayList<>();
        List<Caregiver> areaFiltered = new ArrayList<>();
        List<Caregiver> conditionFiltered = new ArrayList<>();
        List<Caregiver> serviceTypeFiltered = new ArrayList<>();

        for (int index = 0; index < users.size(); index++) {
            User user = users.get(index);

            Set<DayOfWeek> dayOfWeek = new HashSet<>();
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(18, 0);
            String workArea;
            Set<Disease> supportedConditions = new HashSet<>();
            Set<ServiceType> serviceTypes = new HashSet<>();

            // --- 조건별 배정 ---

            if (index < 8) {
                dayOfWeek.addAll(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
            } else {
                dayOfWeek.addAll(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
            }

            if (index >= 8 && index < 15) {
                startTime = LocalTime.of(15, 0);
                endTime = LocalTime.of(18, 0);
            } else {
                startTime = LocalTime.of(9,0);
                endTime = LocalTime.of(12,0);
            }

            if (index >= 15 && index < 21) {
                workArea = pickNonSuncheonAddress(index - 15);
            } else {
                workArea = DUMMY_ADDRESSES[index];
            }

            if (index >= 21 && index < 26) {
                supportedConditions.add(Disease.HYPERTENSION); // 조건 없는 사람
            } else {
                supportedConditions.add(Disease.DEMENTIA);
                supportedConditions.add(Disease.BEDRIDDEN);
            }

            if (index >= 26 && index < 33) {
                serviceTypes.add(ServiceType.VISITING_CARE); // 다른 유형
            } else {
                serviceTypes.add(ServiceType.IN_HOME_SUPPORT);
            }

            // --- Caregiver 생성 ---
            Caregiver caregiver = Caregiver.builder()
                    .caregiverId(UUID.randomUUID())
                    .user(user)
                    .address(workArea)
                    .career(1 + new Random().nextInt(20))
                    .koreanProficiency(KoreanProficiency.values()[new Random().nextInt(KoreanProficiency.values().length)])
                    .isAccompanyOuting(new Random().nextBoolean())
                    .selfIntroduction(DUMMY_INTRODUCTIONS[index])
                    .build();
            caregiver.changeVerifiedStatus(VerifiedStatus.APPROVED);
            caregiverRepository.save(caregiver);

            // CaregiverCenter 생성 (상태 랜덤)
            CaregiverStatus status;
            if (index == 0) {
                status = CaregiverStatus.ACTIVE; // 첫 번째는 무조건 ACTIVE
            } else {
                status = CaregiverStatus.values()[random.nextInt(CaregiverStatus.values().length)];
            }

            // CaregiverCenter 생성
            CaregiverCenter caregiverCenter = CaregiverCenter.builder()
                    .caregiverCenterId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .center(center)
                    .status(status)
                    .build();
            caregiverCenterRepository.save(caregiverCenter);

            // CaregiverPreference 생성
            CaregiverPreference preference = CaregiverPreference.builder()
                    .caregiverPreferenceId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .serviceTypes(serviceTypes)
                    .dayOfWeek(dayOfWeek)
                    .workStartTime(startTime)
                    .workEndTime(endTime)
                    .workMinTime(2 + new Random().nextInt(2))
                    .workMaxTime(4 + new Random().nextInt(4))
                    .availableTime(30 + new Random().nextInt(91))
                    .workArea(workArea)
                    .addressType(new Random().nextBoolean() ? AddressType.ROAD : AddressType.JIBUN)
                    .location(new Location(DUMMY_LATITUDES[index % DUMMY_LATITUDES.length],
                            DUMMY_LONGITUDES[index % DUMMY_LONGITUDES.length]))
                    .transportation(new Random().nextBoolean() ? "자가차량" : "대중교통")
                    .lunchBreak(30)
                    .bufferTime(15)
                    .supportedConditions(supportedConditions)
                    .preferredMinAge(40 + new Random().nextInt(20))
                    .preferredMaxAge(60 + new Random().nextInt(20))
                    .preferredGender(PreferredGender.values()[new Random().nextInt(PreferredGender.values().length)])
                    .build();
            caregiverPreferenceRepository.save(preference);

            // Certification 생성
            Certification certification = Certification.builder()
                    .certificationId(UUID.randomUUID())
                    .caregiver(caregiver)
                    .certificationNumber("CERT-2025-" + String.format("%04d", index))
                    .certificationDate(LocalDate.of(2020 + new Random().nextInt(5),
                            1 + new Random().nextInt(12),
                            1 + new Random().nextInt(28)))
                    .trainStatus(new Random().nextBoolean())
                    .build();
            certificationRepository.save(certification);

            // --- 조건별 필터링 로그 ---
            if (dayOfWeek.size() == 5) dayOfWeekFiltered.add(caregiver);
            if (!(startTime.equals(LocalTime.of(9,0)) && endTime.equals(LocalTime.of(12,0))) ) timeFiltered.add(caregiver);
            if (!workArea.startsWith("전라남도 순천시")) areaFiltered.add(caregiver);
            if (supportedConditions.contains(Disease.HYPERTENSION)) conditionFiltered.add(caregiver);
            if (serviceTypes.contains(ServiceType.VISITING_CARE)) serviceTypeFiltered.add(caregiver);
        }

        // --- 출력 ---
        System.out.println("근무 요일 월~금 아닌 사람: " + dayOfWeekFiltered.size());
        System.out.println("근무 시간이 9~12가 아닌 사람: " + timeFiltered.size());
        System.out.println("근무 지역 순천 외 사람: " + areaFiltered.size());
        System.out.println("지원 가능한 상태 조건이 없는 사람: " + conditionFiltered.size());
        System.out.println("서비스 유형 VISITING_CARE인 사람: " + serviceTypeFiltered.size());
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
