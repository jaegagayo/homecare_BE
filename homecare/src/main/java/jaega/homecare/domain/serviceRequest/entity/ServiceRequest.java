package jaega.homecare.domain.serviceRequest.entity;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.Location;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "serviceRequest")
@NoArgsConstructor
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_request_id", unique = true)
    private UUID serviceRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @Column(name = "service_address", nullable = false)
    private String serviceAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Embedded
    @Column(name = "location", nullable = false)
    private Location location;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;          // 서비스 신청일

    @Column(name = "preferred_start_time", nullable = false)
    private LocalTime preferredStartTime;   // 선호 시간 (시작)

    @Column(name = "preferred_end_time", nullable = false)
    private LocalTime preferredEndTime;     // 선호 시간 (끝)

    @Column(name = "duration", nullable = false)
    private Integer duration;               // 1회 소요 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;        // 서비스 유형

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceRequestStatus requestStatus;    // 신청 상태

    @Column(name = "additional_information")
    private String additionalInformation;

    @Builder
    public ServiceRequest(Consumer consumer, String serviceAddress, AddressType addressType, Location location, LocalTime preferredStartTime, LocalTime preferredEndTime, Integer duration, ServiceType serviceType, LocalDate requestDate, String additionalInformation) {
        this.consumer = consumer;
        this.serviceAddress = serviceAddress;
        this.addressType = addressType;
        this.location = location;
        this.preferredStartTime = preferredStartTime;
        this.preferredEndTime = preferredEndTime;
        this.duration = duration;
        this.serviceType = serviceType;
        this.requestDate = requestDate;
        this.additionalInformation = additionalInformation;
        this.requestStatus = ServiceRequestStatus.PENDING;
    }

    public void initializeServiceRequest(UUID serviceRequestId){
        this.serviceRequestId = serviceRequestId;
    }

    public void changeRequestStatus(ServiceRequestStatus requestStatus){
        this.requestStatus = requestStatus;
    }
}
