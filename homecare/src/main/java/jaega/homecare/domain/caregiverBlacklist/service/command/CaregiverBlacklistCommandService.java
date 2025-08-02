package jaega.homecare.domain.caregiverBlacklist.service.command;

import jaega.homecare.domain.caregiverBlacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.caregiverBlacklist.entity.CaregiverBlacklist;
import jaega.homecare.domain.caregiverBlacklist.mapper.CaregiverBlacklistMapper;
import jaega.homecare.domain.caregiverBlacklist.repository.CaregiverBlacklistRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CaregiverBlacklistCommandService {

    private final CaregiverBlacklistRepository caregiverBlacklistRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final UserQueryService userQueryService;
    private final CaregiverBlacklistMapper caregiverBlacklistMapper;

    public UUID createCaregiverBlacklist(CreateCaregiverBlacklistRequest request) {
        // 연관된 엔티티들 조회
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        User reporter = userQueryService.getUser(request.reporterId());

        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 엔티티 생성
        CaregiverBlacklist caregiverBlacklist = caregiverBlacklistMapper.toEntity(request, caregiver, reporter, now);
        caregiverBlacklist.setBlacklistId(UUID.randomUUID());

        // 저장
        CaregiverBlacklist savedBlacklist = caregiverBlacklistRepository.save(caregiverBlacklist);
        return savedBlacklist.getBlacklistId();
    }
}