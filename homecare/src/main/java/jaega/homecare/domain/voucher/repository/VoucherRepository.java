package jaega.homecare.domain.voucher.repository;

import jaega.homecare.domain.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
