package jaega.homecare.domain.WorkLog.controller;

import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import jaega.homecare.domain.WorkLog.service.query.WorkLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workLog")
public class WorkLogControllerImpl implements WorkLogController{

    private final WorkLogQueryService workLogQueryService;

    @Override
    public ResponseEntity<GetWorkLogResponse> getWorkLog(@PathVariable UUID workLogId) {
        GetWorkLogResponse responses = workLogQueryService.findWorkLog(workLogId);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<GetWorkLogByDateResponse>> getWorkLogByWorkDay(LocalDate workDate) {
        List<GetWorkLogByDateResponse> response = workLogQueryService.getWorkLogsByDate(workDate);
        return ResponseEntity.ok(response);
    }

}
