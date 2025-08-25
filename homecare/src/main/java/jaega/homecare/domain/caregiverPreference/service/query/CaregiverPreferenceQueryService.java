package jaega.homecare.domain.caregiverPreference.service.query;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.mapper.CaregiverPreferenceMapper;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverPreferenceQueryService {
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final CaregiverPreferenceMapper caregiverPreferenceMapper;

    // 도메인 조회 용
    public CaregiverPreference findCaregiverPreferenceByCaregiver(UUID caregiverId){
        return caregiverPreferenceRepository.findByCaregiver_CaregiverId(caregiverId)
                .orElseThrow(() -> new EntityNotFoundException("해당 요양보호사의 선호 조건을 찾을 수 없습니다."));
    }

    // 요양보호사 선호 조건 조회 API
    public GetCaregiverPreferenceResponse getCaregiverPreferenceByCaregiver(UUID caregiverId){
        CaregiverPreference caregiverPreference = findCaregiverPreferenceByCaregiver(caregiverId);
        return caregiverPreferenceMapper.toGetResponse(caregiverPreference);
    }


}
