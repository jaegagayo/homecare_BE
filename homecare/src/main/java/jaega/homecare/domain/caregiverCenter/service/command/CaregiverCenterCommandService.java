package jaega.homecare.domain.caregiverCenter.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.mapper.CaregiverCenterMapper;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.center.entity.Center;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverCenterCommandService {
    private final CaregiverCenterRepository caregiverCenterRepository;
    private final CaregiverCenterMapper caregiverCenterMapper;

    public void createCaregiverCenter(Center center, Caregiver caregiver){
        CaregiverCenter caregiverCenter = caregiverCenterMapper.toEntity(caregiver, center);
        caregiverCenter.setCaregiverCenter(UUID.randomUUID(), CaregiverStatus.ACTIVE, LocalDateTime.now());

        caregiverCenterRepository.save(caregiverCenter);
    }
}
