package jaega.homecare.domain.caregiverCenter.service.query;

import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaregiverCenterQueryService {
    private final CaregiverCenterRepository caregiverCenterRepository;
}
