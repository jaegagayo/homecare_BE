package jaega.homecare.domain.center.service.query;

import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.repository.CenterRepository;
import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CenterQueryService {

    private final CenterRepository centerRepository;

    public Center getCenterByUUID(UUID centerId) {
        return centerRepository.findByCenterId(centerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 centerId로 센터를 찾을 수 없습니다."));
    }

    public Center getCenterByUser(User user){
        return centerRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저 정보로 센터의 id를 가져올 수 없습니다."));
    }
}
