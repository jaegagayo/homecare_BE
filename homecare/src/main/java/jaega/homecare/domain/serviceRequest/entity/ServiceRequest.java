package jaega.homecare.domain.serviceRequest.entity;

import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "serviceRequest")
@NoArgsConstructor
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID serviceRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_userId")
    private User user;

    private String location;

    private LocalDateTime preferred_time_start;

    private LocalDateTime preferred_time_end;

    private String serviceType;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;

    private String personalityType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_request_days", joinColumns = @JoinColumn(name = "service_request_id"))
    @Column(name = "requested_days")
    private Set<Integer> requestedDays; // ex : 1, 3, 5, ...

    @Builder
    public ServiceRequest(UUID serviceRequestId, User user, String location, LocalDateTime preferred_time_start, LocalDateTime preferred_time_end,
                          String serviceType, ServiceRequestStatus status, String personalityType, Set<Integer> requestedDays){
        this.location = location;
        this.preferred_time_start = preferred_time_start;
        this.preferred_time_end = preferred_time_end;
        this.serviceType = serviceType;
        this.personalityType = personalityType;
        this.requestedDays = requestedDays;
    }

    public void setServiceRequest(UUID serviceRequestId, User user, ServiceRequestStatus status, Set<Integer> requestedDays){
        this.serviceRequestId = serviceRequestId;
        this.user = user;
        this.status = status;
        this.requestedDays = requestedDays;
    }
}
