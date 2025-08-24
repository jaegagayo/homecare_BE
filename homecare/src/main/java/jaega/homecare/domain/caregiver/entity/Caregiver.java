package jaega.homecare.domain.caregiver.entity;

import jaega.homecare.domain.center.dto.req.CreateCaregiverProfileRequest;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.global.audit.BaseTimeEntity;
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
@Table(name = "caregiver")
@NoArgsConstructor
public class Caregiver extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "caregiver_id", unique = true)
    private UUID caregiverId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 근무 시작 시간
    @Column(name = "available_start_time")
    private LocalTime availableStartTime;

    // 근무 종료 시간
    @Column(name = "available_end_time")
    private LocalTime availableEndTime;

    @Column(name = "address")
    private String address;

    @Embedded
    private Location location;

    // 서비스 유형 (다중 선택 가능)
    @ElementCollection(targetClass = ServiceType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "service_type", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private Set<ServiceType> serviceTypes = new HashSet<>();

    // 근무 가능 요일
    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "caregiver_day_of_week", joinColumns = @JoinColumn(name = "caregiver_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> dayOfWeek = new HashSet<>();

    @Builder
    public Caregiver(UUID caregiverId, User user, LocalTime availableStartTime, LocalTime availableEndTime, String address, Location location, Set<ServiceType> serviceTypes, Set<DayOfWeek> dayOfWeek) {
        this.caregiverId = caregiverId;
        this.user = user;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.address = address;
        this.location = location;
        this.serviceTypes = serviceTypes;
        this.dayOfWeek = dayOfWeek;
    }

    public void initializeCaregiver(UUID uuid) {
        this.caregiverId = uuid;
    }

    public void setCaregiverProfile(CreateCaregiverProfileRequest request){
        this.availableStartTime = request.availableStartTIme();
        this.availableEndTime = request.availableEndTime();
        this.address = request.address();
        this.location = request.location();
        this.serviceTypes = request.serviceTypes();
        this.dayOfWeek = request.dayOfWeek();
    }
}
