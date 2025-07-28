package jaega.homecare.domain.serviceRequest.service.command;

import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.mapper.ServiceRequestMapper;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestCommandService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserQueryService userQueryService;
    private final ServiceRequestMapper serviceRequestMapper;

    @Transactional
    public void createServiceRequest(ConsumerServiceRequest request){
        User user = userQueryService.getUser(request.userId());

        ServiceRequest serviceRequest = serviceRequestMapper.toEntity(request);
        Set<Integer> requestedDaysSet = parseRequestedDays(request.requestedDays());

        serviceRequest.setServiceRequest(UUID.randomUUID(), user, ServiceRequestStatus.PENDING, requestedDaysSet);
        serviceRequestRepository.save(serviceRequest);
    }

    private Set<Integer> parseRequestedDays(String requestedDaysStr) {
        if (requestedDaysStr == null || requestedDaysStr.isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(requestedDaysStr.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
