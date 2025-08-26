package jaega.homecare.domain.voucherUsage.entity;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "voucher_usage")
@NoArgsConstructor
public class VoucherUsage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "voucher_usage_id", unique = true)
    private UUID voucherUsageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_match_id")
    private ServiceMatch serviceMatch;

    @Column(name = "amount", nullable = false)
    private Long amount;                        // 사용 금액

    @Column(name = "copay", nullable = false)
    private Long copay;                         // 본인 부담금

}
