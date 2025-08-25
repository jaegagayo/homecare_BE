package jaega.homecare.domain.recurringOffer.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "recurring_offer")
@NoArgsConstructor
public class RecurringOffer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "recurring_offer_id", unique = true)
    private UUID recurringOfferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    // 근무 가능 요일
    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "recurring_offer_day_of_week", joinColumns = @JoinColumn(name = "recurring_offer_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> dayOfWeek = new HashSet<>();

    @JoinColumn(name = "service_start_date")
    private LocalDate serviceStartDate; // 서비스 시작 날짜

    @JoinColumn(name = "service_end_date")
    private LocalDate serviceEndDate; // 서비스 종료 날짜

    @JoinColumn(name = "service_start_time")
    private LocalTime serviceStartTime; // 서비스 시작 날짜

    @JoinColumn(name = "service_end_time")
    private LocalTime serviceEndTime; // 서비스 종료 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;        // 서비스 유형

    @Column(name = "recurring_status")
    private RecurringStatus recurringStatus = RecurringStatus.PENDING;

    // TODO : 알림 확인 여부, 추후 이벤트 큐 구조 혹은 알림 기능 도입 시 해당 속성 삭제될 수 있음
    @Column(name = "recurring_offer_unread", nullable = false)
    private boolean recurringOfferUnread = true;

    @Builder
    public RecurringOffer(UUID recurringOfferId, Caregiver caregiver, Consumer consumer,
                          Set<DayOfWeek> dayOfWeek, LocalDate serviceStartDate, LocalDate serviceEndDate,
                          LocalTime serviceStartTime, LocalTime serviceEndTime, ServiceType serviceType,
                          RecurringStatus recurringStatus, boolean recurringOfferUnread){
        this.recurringOfferId = recurringOfferId;
        this.caregiver = caregiver;
        this.consumer = consumer;
        this.dayOfWeek = dayOfWeek;
        this.serviceStartDate = serviceStartDate;
        this.serviceEndDate = serviceEndDate;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.serviceType = serviceType;
    }

    public void initializeRecurringOffer(UUID recurringOfferId){
        this.recurringOfferId = recurringOfferId;
    }

    public void readRecurringOfferDetail(){
        this.recurringOfferUnread = false;
    }

}
