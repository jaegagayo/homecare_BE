package jaega.homecare.domain.voucher.entity;


import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "voucher")
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "voucher_id", unique = true)
    private UUID voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Builder
    public Voucher(UUID voucherId, Consumer consumer, LocalDate voucherDate, Long totalAmount){
        this.voucherId = voucherId;
        this.consumer = consumer;
        this.voucherDate = voucherDate;
        this.totalAmount = totalAmount;
    }
}
