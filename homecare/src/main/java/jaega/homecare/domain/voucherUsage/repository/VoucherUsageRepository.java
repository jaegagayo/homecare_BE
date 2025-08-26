package jaega.homecare.domain.voucherUsage.repository;

import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherUsageRepository extends JpaRepository <VoucherUsage, Long>{
}
