package jaega.homecare.domain.serviceMatch.service.query;

import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.mapper.ServiceMatchMapper;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceMatchQueryService {

    private final ServiceMatchRepository serviceMatchRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final ServiceMatchMapper serviceMatchMapper;

    // 도메인 조회
    public ServiceMatch getServiceMatch(UUID serviceMatchId){
        return serviceMatchRepository.findByServiceMatchId(serviceMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 serviceMatchId로 서비스 매칭 결과를 찾을 수 없습니다."));
    }

    // UUID 기반 매칭 결과 조회
    public GetServiceMatchByUUID getMatchesByUUID(UUID serviceMatchId){
        ServiceMatch serviceMatch = getServiceMatch(serviceMatchId);
        return serviceMatchMapper.toGetResponseByUUID(serviceMatch);
    }

//    // Center 기반 매칭 결과 조회
//    public List<GetServiceMatchByCenterResponse> getMatchesByCenter(UUID centerId) {
//        return serviceMatchQueryRepository.findMatchesByCenterId(centerId);
//    }


}
