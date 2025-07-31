package jaega.homecare.domain.WorkMatch.service.command;

import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.mapper.WorkMatchMapper;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkMatchCommandService {

    private final WorkMatchRepository workMatchRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final WorkMatchMapper workMatchMapper;

    public void createWorkMatch(CreateWorkMatchRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        List<LocalDate> workingDays = request.working_days();
        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalArgumentException("working_day 리스트는 비어 있을 수 없습니다.");
        }

        List<WorkMatch> workMatches = workingDays.stream()
                .map(day -> {
                    WorkMatch workMatch = workMatchMapper.toEntity(caregiver, day, request.workTime_start(), request.workTime_end());
                    workMatch.setWorkMatch(UUID.randomUUID());
                    return workMatch;
                })
                .toList();

        workMatchRepository.saveAll(workMatches);

    }

}
