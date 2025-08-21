package jaega.homecare.domain.workLog.service.command;

import jaega.homecare.domain.workLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.workLog.entity.WorkLog;
import jaega.homecare.domain.workLog.entity.WorkStatus;
import jaega.homecare.domain.workLog.mapper.WorkLogMapper;
import jaega.homecare.domain.workLog.repository.WorkLogRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
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
    private final CaregiverQueryService caregiverQueryService;
    private final WorkLogMapper workLogMapper;


    public void createWorkMatch(CreateWorkLogRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        BigDecimal settlementAmount = calculateSettlementAmount(request.workStartTime(), request.workEndTime(), request.distanceLog());

        WorkLog workLog = workLogMapper.toEntity(request, caregiver, settlementAmount);
        workLog.initializeWorkLogId(UUID.randomUUID());
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

    /**
     * 특정 WorkMatch의 상태 변경
     */
    public void changeWorkLogStatus(UUID workLogId, WorkStatus newStatus) {
        WorkLog workLog = workLogRepository.findByWorkLogId(workLogId)
                .orElseThrow(() -> new IllegalArgumentException("WorkLog not found: " + workLogId));

        workLog.changeWorkStatus(newStatus);
    }

    public void changePaidStatus(UUID workLogId) {
        WorkLog workLog = workLogRepository.findByWorkLogId(workLogId)
                .orElseThrow(() -> new IllegalArgumentException("WorkLog not found: " + workLogId));

        workLog.changePaidStatus();
    }

}
