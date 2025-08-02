package jaega.homecare.domain.caregiverBlacklist.service.query;

import jaega.homecare.domain.caregiverBlacklist.dto.res.GetCaregiverBlacklistResponse;
import jaega.homecare.domain.caregiverBlacklist.entity.CaregiverBlacklist;
import jaega.homecare.domain.caregiverBlacklist.mapper.CaregiverBlacklistMapper;
import jaega.homecare.domain.caregiverBlacklist.repository.CaregiverBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverBlacklistQueryService {

    private final CaregiverBlacklistRepository caregiverBlacklistRepository;
    private final CaregiverBlacklistMapper caregiverBlacklistMapper;

    // 특정 신고자의 블랙리스트 조회
    public List<GetCaregiverBlacklistResponse> getCaregiverBlacklistsByReporterId(UUID reporterId) {
        List<CaregiverBlacklist> blacklists = caregiverBlacklistRepository.findByReporter_UserId(reporterId);
        return blacklists.stream()
                .map(caregiverBlacklistMapper::toGetResponse)
                .toList();
    }
}