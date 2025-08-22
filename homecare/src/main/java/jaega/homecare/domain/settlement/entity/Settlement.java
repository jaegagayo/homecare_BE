package jaega.homecare.domain.settlement.entity;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "settlement")
@NoArgsConstructor
public class Settlement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "settlement_id", unique = true)
    private UUID settlementId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceMatch_id")
    private ServiceMatch serviceMatch;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiverCenter_id")
    private CaregiverCenter caregiverCenter;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    @Column(name = "distance_log")
    private Double distanceLog;

    @Builder

    public Settlement(UUID settlementId, ServiceMatch serviceMatch, CaregiverCenter caregiverCenter,
                      boolean isPaid, BigDecimal settlementAmount, Double distanceLog){
        this.settlementId = settlementId;
        this.serviceMatch = serviceMatch;
        this.caregiverCenter = caregiverCenter;
        this.isPaid = isPaid;
        this.settlementAmount = settlementAmount;
        this.distanceLog = distanceLog;
    }

    public void initializeSettlement(UUID settlementId){
        this.settlementId = settlementId;
    }

    public void changePaidStatus(){
        this.isPaid = !isPaid;
    }

}
