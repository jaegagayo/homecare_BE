package jaega.homecare.domain.caregiverCenter.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.center.entity.Center;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "caregiver_center")
@NoArgsConstructor
public class CaregiverCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "caregiver_center_id", unique = true)
    private UUID caregiverCenterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Enumerated(EnumType.STRING)
    private CaregiverStatus status;

    private LocalDateTime joinedAt;

    @Builder
    public CaregiverCenter(UUID caregiverCenterId, Caregiver caregiver, Center center, CaregiverStatus status, LocalDateTime joinedAt){
        this.caregiverCenterId = caregiverCenterId;
        this.caregiver = caregiver;
        this.center = center;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public void setCaregiverCenter(UUID caregiverCenterId, CaregiverStatus status, LocalDateTime joinedAt){
        this.caregiverCenterId = caregiverCenterId;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public void deregister() {
        this.status = CaregiverStatus.INACTIVE;
    }

}
