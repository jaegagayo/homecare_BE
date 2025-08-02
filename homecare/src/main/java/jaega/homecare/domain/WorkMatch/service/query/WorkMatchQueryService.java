package jaega.homecare.domain.WorkMatch.service.query;

import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.mapper.WorkMatchMapper;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchQueryRepository;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkMatchQueryService {

    private final WorkMatchRepository workMatchRepository;
    private final WorkMatchQueryRepository workMatchQueryRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;

    public WorkMatch getWorkMatch(UUID workMatchId){
        return workMatchRepository.findByWorkMatchId(workMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 workMatchId로 근무 매칭 정보를 찾을 수 없습니다."));
    }

    public List<GetCaregiverMatchesResponse> getWorkMatchesByCaregiver(UUID caregiverId){
        return serviceMatchQueryRepository.findByCaregiverId(caregiverId);
    }

    public List<GetCaregiverMatchesByMonth> getWorkMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        return workMatchQueryRepository.findWorkMatchesByMonth(centerId, year, month, day);
    }

}
