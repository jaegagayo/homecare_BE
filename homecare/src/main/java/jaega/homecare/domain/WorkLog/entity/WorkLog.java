package jaega.homecare.domain.WorkLog.entity;

import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "workLog")
@NoArgsConstructor
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workLog_id", unique = true)
    private UUID workLogId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workMatch_id")
    private WorkMatch workMatch;

    @Column(name = "work_time_start")
    private LocalTime workTime_start;

    @Column(name = "work_time_end")
    private LocalTime workTime_end;

    @Column(name = "distance_log")
    private Double distanceLog;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Builder
    public WorkLog(UUID workLogId, WorkMatch workMatch, LocalTime workTime_start,
                   LocalTime workTime_end, Double distanceLog, boolean isPaid){
        this.workLogId = workLogId;
        this.workMatch = workMatch;
        this.workTime_start = workTime_start;
        this.workTime_end = workTime_end;
        this.distanceLog = distanceLog;
        this.isPaid = isPaid;
    }

    public void setWorkLog(UUID workLogId){
        this.workLogId = workLogId;
    }
}
