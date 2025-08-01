package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverQueryService {

    private final CaregiverQueryRepository caregiverQueryRepository;
    private final CaregiverRepository caregiverRepository;

    public List<GetCaregiverResponse> getAllCaregiversByCenter(UUID centerId) {
        return caregiverQueryRepository.findAllByCenterId(centerId);
    }

    public Caregiver getCaregiver(UUID caregiverId){
        return caregiverRepository.findByCaregiverId(caregiverId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요양보호사입니다."));
    }
}
