package jaega.homecare.domain.workMatch.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "workMatch")
@NoArgsConstructor
public class WorkMatch extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workMatch_id", unique = true)
    private UUID workMatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalTime workStartTime;

    @Column(name = "end_time")
    private LocalTime workEndTime;

    @Column(name = "distance_log")
    private Double distanceLog;

    @Column(name = "work_address")
    private String workAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkStatus status;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Column(name = "settlement_account")
    private BigDecimal settlementAmount;

    @Builder
    public WorkMatch(UUID workMatchId, Caregiver caregiver, LocalDate workDate, LocalTime workStartTime, LocalTime workEndTime, Double distanceLog, String workAddress, boolean isPaid, BigDecimal settlementAmount) {
        this.workMatchId = workMatchId;
        this.caregiver = caregiver;
        this.workDate = workDate;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.distanceLog = distanceLog;
        this.workAddress = workAddress;
        this.status = WorkStatus.PLANNED;
        this.isPaid = isPaid;
        this.settlementAmount = settlementAmount;
    }

    public void initializeWorkMatch(UUID workMatchId){
        this.workMatchId = workMatchId;
    }

    public void changePaidStatus(){
        this.isPaid = !isPaid;
    }

    public void changeWorkStatus(WorkStatus newStatus) {
        this.status = newStatus;
    }
}
