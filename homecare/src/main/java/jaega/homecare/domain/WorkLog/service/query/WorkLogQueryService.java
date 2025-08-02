package jaega.homecare.domain.WorkLog.service.query;

import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkLog.mapper.WorkLogMapper;
import jaega.homecare.domain.WorkLog.repository.WorkLogQueryRepository;
import jaega.homecare.domain.WorkLog.repository.WorkLogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkLogQueryService {
    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;
    private final WorkLogQueryRepository workLogQueryRepository;

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
}
