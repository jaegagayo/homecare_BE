package jaega.homecare.domain.workMatch.service.command;

import jaega.homecare.domain.workMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.workMatch.entity.WorkMatch;
import jaega.homecare.domain.workMatch.entity.WorkStatus;
import jaega.homecare.domain.workMatch.mapper.WorkMatchMapper;
import jaega.homecare.domain.workMatch.repository.WorkMatchRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkMatchCommandService {

    private final WorkMatchRepository workMatchRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final WorkMatchMapper workMatchMapper;

    /**
     * 특정 WorkMatch의 근무 상태 변경
     */
    public void changeWorkMatchStatus(UUID workMatchId, WorkStatus newStatus) {
        WorkMatch workMatch = workMatchRepository.findByWorkMatchId(workMatchId)
                .orElseThrow(() -> new IllegalArgumentException("WorkMatch not found: " + workMatchId));

        workMatch.changeWorkStatus(newStatus);
    }

    public void createWorkMatch(CreateWorkMatchRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        Set<LocalDate> workingDays = request.working_days();
        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalArgumentException("working_day 리스트는 비어 있을 수 없습니다.");
        }

        BigDecimal settlementAmount = calculateSettlementAmount(request.workTime_start(), request.workTime_end(), request.distanceLog());

        List<WorkMatch> workMatches = workingDays.stream()
                .map(day -> {
                    WorkMatch workMatch = workMatchMapper.toEntity(request, caregiver, day, settlementAmount);
                    workMatch.setWorkMatch(UUID.randomUUID());
                    return workMatch;
                })
                .toList();
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
     * 특정 WorkLog의 정산 상태 변경
     */
    public void togglePaidStatus(UUID workMatchId) {
        WorkMatch workMatch = workMatchRepository.findByWorkMatchId(workMatchId)
                .orElseThrow(() -> new IllegalArgumentException("WorkLog not found: " + workMatchId));

        workMatch.togglePaidStatus();
    }

}
