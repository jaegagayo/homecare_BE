package jaega.homecare.domain.serviceMatch.service.query;

import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByConsumerResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceMatchQueryService {

    private final ServiceMatchRepository serviceMatchRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;

    public List<GetServiceMatchByCenterResponse> getMatchesByCenter(UUID centerId) {
        return serviceMatchQueryRepository.findMatchesByCenterId(centerId);
    }

    public List<GetServiceMatchByConsumerResponse> getMatchesByConsumer(UUID userId){
        return serviceMatchQueryRepository.findByUserId(userId);
    }

}
