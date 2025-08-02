package jaega.homecare.domain.serviceRequest.entity;

import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private String address;

    @Embedded
    private Location location;

    private LocalTime preferred_time_start;

    private LocalTime preferred_time_end;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;

    private String personalityType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_request_days", joinColumns = @JoinColumn(name = "service_request_id"))
    @Column(name = "requested_days")
    private Set<LocalDate> requestedDays; // ex : 1, 3, 5, ...

    private String additionalInformation;

    @Builder
    public ServiceRequest(UUID serviceRequestId, User user, String address, Location location, LocalTime preferred_time_start, LocalTime preferred_time_end,
                          ServiceType serviceType, ServiceRequestStatus status, String personalityType, Set<LocalDate> requestedDays, String additionalInformation){
        this.address = address;
        this.location = location;
        this.preferred_time_start = preferred_time_start;
        this.preferred_time_end = preferred_time_end;
        this.serviceType = serviceType;
        this.personalityType = personalityType;
        this.requestedDays = requestedDays;
        this.additionalInformation = additionalInformation;
    }

    public void setServiceRequest(UUID serviceRequestId, User user, ServiceRequestStatus status, Set<LocalDate> requestedDays){
        this.serviceRequestId = serviceRequestId;
        this.user = user;
        this.status = status;
        this.requestedDays = requestedDays;
    }
}
