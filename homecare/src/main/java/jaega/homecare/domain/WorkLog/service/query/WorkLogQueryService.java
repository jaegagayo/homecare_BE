package jaega.homecare.domain.WorkLog.service.query;

import jaega.homecare.domain.WorkLog.dto.res.GetDashboardSettlementResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkLog.mapper.WorkLogMapper;
import jaega.homecare.domain.WorkLog.repository.WorkLogQueryRepository;
import jaega.homecare.domain.WorkLog.repository.WorkLogRepository;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkLogQueryService {
    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;
    private final WorkLogQueryRepository workLogQueryRepository;
    private final CenterQueryService centerQueryService;

    // 엔티티 조회용
    public WorkLog getWorkLog(UUID workLogId){
        return workLogRepository.findByWorkLogId(workLogId)
                .orElseThrow(() -> new EntityNotFoundException("해당 workLogId로 근무 기록을 찾을 수 없습니다."));
    }

    // 프론트엔드 GET 조회용
    public GetWorkLogResponse findWorkLog(UUID workLogId){
        WorkLog workLogs = getWorkLog(workLogId);

        return workLogMapper.toGetResponse(workLogs);

    }

    public List<GetWorkLogByDateResponse> getWorkLogsByDate(UUID centerId, LocalDate date) {
        return workLogQueryRepository.findWorkLogsByDate(centerId, date);
    }

    public List<GetWorkLogByPaid> getWorkLogByPaid(UUID centerId, Boolean isPaid){
        return workLogQueryRepository.findWorkLogsByPaid(centerId, isPaid);
    }

    public GetDashboardSettlementResponse getSettlementStatus(UUID centerId) {

        BigDecimal totalSettledAmount = workLogQueryRepository.getTotalSettledAmountThisMonth(centerId);
        if (totalSettledAmount == null) {
            totalSettledAmount = BigDecimal.ZERO;
        }
        Long unsettledCount = workLogQueryRepository.countUnsettled(centerId);
        if (unsettledCount == null) {
            unsettledCount = 0L;
        }

        // TODO: 부정행위 알림 건수 조회 메서드 추가 시 적용할 것!
        Long fraudAlertsCount = 0L;

        return new GetDashboardSettlementResponse(totalSettledAmount, unsettledCount, fraudAlertsCount);
    }
}
