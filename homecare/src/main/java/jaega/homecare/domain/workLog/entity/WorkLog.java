package jaega.homecare.domain.workLog.entity;

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
@Table(name = "workLog")
@NoArgsConstructor
public class WorkLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workLog_id", unique = true)
    private UUID workLogId;

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
    public WorkLog(UUID workLogId, Caregiver caregiver, LocalDate workDate, LocalTime workStartTime, LocalTime workEndTime, Double distanceLog, String workAddress, boolean isPaid, BigDecimal settlementAmount) {
        this.workLogId = workLogId;
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

    public void initializeWorkLogId(UUID workLogId){
        this.workLogId = workLogId;
    }

    public void changePaidStatus(){
        this.isPaid = !isPaid;
    }

    public void changeWorkStatus(WorkStatus newStatus) {
        this.status = newStatus;
    }
}