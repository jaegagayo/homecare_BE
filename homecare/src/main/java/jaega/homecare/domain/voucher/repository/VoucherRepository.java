package jaega.homecare.domain.voucher.repository;

import jaega.homecare.domain.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByVoucherId(UUID voucherId);

}
