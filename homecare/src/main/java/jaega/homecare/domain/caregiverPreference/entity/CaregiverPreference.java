package jaega.homecare.domain.caregiverPreference.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.ServiceType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "caregiverPreference")
@NoArgsConstructor
public class CaregiverPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "caregiver_preference_id", unique = true)
    private UUID caregiverPreferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    // 근무 가능 요일
    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "caregiver_day_of_week", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> dayOfWeek = new HashSet<>();

    @Column(name = "work_start_time")
    private LocalTime workStartTime;

    @Column(name = "work_end_time")
    private LocalTime workEndTime;

    @Column(name = "work_min_time")
    private Integer workMinTime;

    @Column(name = "work_max_time")
    private Integer workMaxTime;

    @Column(name = "available_time")
    private Integer availableTime;

    // serviceRequest의 service_address
    @Column(name = "work_area")
    private String workArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;

    @Embedded
    @Column(name = "location")
    private Location location;

    @Column(name = "transportation")
    private String transportation;

    @Column(name = "lunch_break")
    private Integer lunchBreak;

    @Column(name = "buffer_time")
    private Integer bufferTime;

    // 지원 가능 질환
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "caregiver_supported_conditions", joinColumns = @JoinColumn(name = "caregiver_preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "supported_conditions")
    private Set<Disease> supportedConditions = new HashSet<>();

    // 선호 연령
    @Column(name = "preferred_min_age")
    private Integer preferredMinAge;

    @Column(name = "preferred_max_age")
    private Integer preferredMaxAge;

    // 선호 성별
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_gender")
    private PreferredGender preferredGender;

    // 서비스 유형 (다중 선택 가능)
    @ElementCollection(targetClass = ServiceType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "service_type", joinColumns = @JoinColumn(name = "caregiver_preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "caregiver_service_type")
    private Set<ServiceType> serviceTypes = new HashSet<>();

    @Builder
    public CaregiverPreference(UUID caregiverPreferenceId, Caregiver caregiver, Set<DayOfWeek> dayOfWeek,
                               LocalTime workStartTime, LocalTime workEndTime, Integer workMinTime, Integer workMaxTime,
                               Integer availableTime, String workArea, AddressType addressType, Location location,
                               String transportation, Integer lunchBreak, Integer bufferTime, Set<Disease> supportedConditions,
                               Integer preferredMaxAge, Integer preferredMinAge, PreferredGender preferredGender, Set<ServiceType> serviceTypes){
        this.caregiverPreferenceId = caregiverPreferenceId;
        this.caregiver = caregiver;
        this.dayOfWeek = dayOfWeek;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workMinTime = workMinTime;
        this.workMaxTime = workMaxTime;
        this.availableTime = availableTime;
        this.workArea = workArea;
        this.addressType = addressType;
        this.location = location;
        this.transportation = transportation;
        this.lunchBreak = lunchBreak;
        this.bufferTime = bufferTime;
        this.supportedConditions = supportedConditions;
        this.preferredMinAge = preferredMinAge;
        this.preferredMaxAge = preferredMaxAge;
        this.preferredGender = preferredGender;
        this.serviceTypes = serviceTypes;
    }

    public void initializeCaregiverPreference(UUID caregiverPreferenceId){
        this.caregiverPreferenceId = caregiverPreferenceId;
    }


}
