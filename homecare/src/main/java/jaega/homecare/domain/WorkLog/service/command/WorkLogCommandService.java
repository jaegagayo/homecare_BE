package jaega.homecare.domain.WorkLog.service.command;

import jaega.homecare.domain.WorkLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkLog.mapper.WorkLogMapper;
import jaega.homecare.domain.WorkLog.repository.WorkLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkLogCommandService {

    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;

    /**
     * 특정 WorkLog의 정산 상태 변경
     */
    public void togglePaidStatus(UUID workLogId) {
        WorkLog workLog = workLogRepository.findByWorkLogId(workLogId)
                .orElseThrow(() -> new IllegalArgumentException("WorkLog not found: " + workLogId));

        workLog.togglePaidStatus();
    }

    public void createWorkLog(CreateWorkLogRequest request){

        BigDecimal settlementAccount = calculateSettlementAmount(request.workTime_start(), request.workTime_end(), request.distanceLog());

        WorkLog workLog = workLogMapper.toEntity(request);
        workLog.setWorkLog(UUID.randomUUID(), settlementAccount);
        workLogRepository.save(workLog);
    }

    public BigDecimal calculateSettlementAmount(LocalTime start, LocalTime end, Double distanceKm) {
        BigDecimal hourlyRate = new BigDecimal("15000");  // 시간당 단가
        BigDecimal distanceRate = new BigDecimal("500");  // 거리당 단가

        // 근무 시간 계산 (분 단위 -> 시간 단위로 변환)
        long minutes = Duration.between(start, end).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        // 거리
        BigDecimal distance = BigDecimal.valueOf(distanceKm != null ? distanceKm : 0);

        // 금액 계산
        BigDecimal amount = hourlyRate.multiply(hours).add(distanceRate.multiply(distance));
        return amount.setScale(0, RoundingMode.HALF_UP);  // 반올림, 소수점 없애기
    }
}
