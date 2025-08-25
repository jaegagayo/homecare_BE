package jaega.homecare.domain.caregiver.entity;

import jaega.homecare.domain.users.entity.User;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
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

    @Column(name = "career")
    private Integer career;

    @Enumerated(EnumType.STRING)
    @Column(name = "korean_proficiency")
    private KoreanProficiency koreanProficiency;

    @Column(name = "is_accompany_outing")
    private boolean isAccompanyOuting;

    @Column(name = "self_introduction", length = 1000)
    private String selfIntroduction;

    @Enumerated(EnumType.STRING)
    private VerifiedStatus verifiedStatus;

    @Builder
    public Caregiver(UUID caregiverId, User user, LocalTime availableStartTime, LocalTime availableEndTime,
                     String address, Integer career,
                     KoreanProficiency koreanProficiency, boolean isAccompanyOuting, String selfIntroduction, VerifiedStatus verifiedStatus) {
        this.caregiverId = caregiverId;
        this.user = user;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.address = address;
        this.career = career;
        this.koreanProficiency = koreanProficiency;
        this.isAccompanyOuting = isAccompanyOuting;
        this.selfIntroduction = selfIntroduction;
        this.verifiedStatus = VerifiedStatus.PENDING;
    }

    public void initializeCaregiver(UUID uuid) {
        this.caregiverId = uuid;
    }
}
