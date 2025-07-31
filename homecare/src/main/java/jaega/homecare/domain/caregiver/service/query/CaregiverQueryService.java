package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverQueryService {

    private final CaregiverQueryRepository caregiverRepository;

    public List<GetCaregiverResponse> getAllCaregiversByCenter(UUID centerId) {
        return caregiverRepository.findAllByCenterId(centerId);
    }
}
