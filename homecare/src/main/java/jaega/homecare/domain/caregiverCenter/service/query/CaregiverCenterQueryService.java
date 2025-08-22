package jaega.homecare.domain.caregiverCenter.service.query;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverCenterQueryService {
    private final CaregiverCenterRepository caregiverCenterRepository;

    public CaregiverCenter getCaregiverCenter(UUID caregiverCenterId){
        return caregiverCenterRepository.findByCaregiverCenterId(caregiverCenterId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID로 특정 센터의 요양보호사를 찾을 수 없습니다."));
    }
}
