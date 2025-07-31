package jaega.homecare.domain.WorkMatch.service.query;

import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkMatchQueryService {

    private final WorkMatchRepository workMatchRepository;
}
