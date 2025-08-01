package jaega.homecare.domain.serviceMatch.entity;


import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "service_match")
@NoArgsConstructor
public class ServiceMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "service_match_id", unique = true)
    private UUID serviceMatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status")
    private MatchStatus status;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Builder
    public ServiceMatch(UUID serviceMatchId, ServiceRequest serviceRequest, Caregiver caregiver, MatchStatus status, LocalDate serviceDate, LocalTime startTime, LocalTime endTime) {
        this.serviceMatchId = serviceMatchId;
        this.serviceRequest = serviceRequest;
        this.caregiver = caregiver;
        this.status = MatchStatus.PENDING;
        this.serviceDate = serviceDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setServiceMatch(UUID serviceMatchId){
        this.serviceMatchId = serviceMatchId;
    }
}