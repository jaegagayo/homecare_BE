package jaega.homecare.domain.serviceMatch.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.mapper.ServiceMatchMapper;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceMatchCommandService {

    private final ServiceMatchRepository serviceMatchRepository;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final CaregiverQueryService caregiverQueryService;
    private final ServiceMatchMapper serviceMatchMapper;

    public void createServiceMatch(CreateServiceMatchRequest request){
        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(request.serviceRequestId());
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        Set<LocalDate> serviceDays = request.service_days();
        if(serviceDays == null || serviceDays.isEmpty()){
            throw new IllegalArgumentException("service_days 리스트는 비어 있을 수 없습니다.");
        }

        List<ServiceMatch> serviceMatches = serviceDays.stream()
                        .map(day -> {
                            ServiceMatch serviceMatch = serviceMatchMapper.toEntity(request, day, serviceRequest, caregiver);
                            serviceMatch.setServiceMatch(UUID.randomUUID());
                            return serviceMatch;
                        })
                        .toList();

        serviceMatchRepository.saveAll(serviceMatches);

    }
}
