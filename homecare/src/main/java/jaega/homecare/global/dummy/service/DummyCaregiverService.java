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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static jaega.homecare.global.dummy.service.DummyData.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyCaregiverService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final CertificationRepository certificationRepository;
    private final Random random = new Random();

    protected void generateDummyCaregiverForLogging(){
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
}
