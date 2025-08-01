package jaega.homecare.domain.serviceMatch.service.query;

import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceMatchQueryService {

    private final ServiceMatchRepository serviceMatchRepository;
}
