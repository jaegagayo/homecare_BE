package jaega.homecare.domain.WorkLog.service.command;

import jaega.homecare.domain.WorkLog.dto.req.CreateWorkLogRequest;
import jaega.homecare.domain.WorkLog.entity.WorkLog;
import jaega.homecare.domain.WorkLog.mapper.WorkLogMapper;
import jaega.homecare.domain.WorkLog.repository.WorkLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkLogCommandService {

    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;

    public void createWorkLog(CreateWorkLogRequest request){

        WorkLog workLog = workLogMapper.toEntity(request);
        workLog.setWorkLog(UUID.randomUUID());
        workLogRepository.save(workLog);
    }
}
