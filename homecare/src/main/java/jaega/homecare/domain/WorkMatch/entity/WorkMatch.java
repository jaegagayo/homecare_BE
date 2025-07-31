package jaega.homecare.domain.WorkMatch.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "workMatch")
@NoArgsConstructor
public class WorkMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workMatch_id", unique = true)
    private UUID workMatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @Column(name = "work_date")
    private LocalDate workingDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkStatus status;

    @Builder
    public WorkMatch(UUID workMatchId, Caregiver caregiver, LocalDate workingDate, LocalTime startTime, LocalTime endTime, WorkStatus status){
        this.workMatchId = workMatchId;
        this.caregiver = caregiver;
        this.workingDate = workingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = WorkStatus.PLANNED;
    }

    public void setWorkMatch(UUID workMatchId){
        this.workMatchId = workMatchId;
    }
}
