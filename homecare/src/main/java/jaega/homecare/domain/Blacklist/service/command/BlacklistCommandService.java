package jaega.homecare.domain.Blacklist.service.command;

import jaega.homecare.domain.Blacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.Blacklist.entity.Blacklist;
import jaega.homecare.domain.Blacklist.mapper.BlacklistMapper;
import jaega.homecare.domain.Blacklist.repository.BlacklistRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.Blacklist.service.query.BlacklistQueryService;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistCommandService {

    private final BlacklistRepository blacklistRepository;
    private final CaregiverQueryService caregiverQueryService;
    private final ConsumerQueryService consumerQueryService;
    private final BlacklistMapper blacklistMapper;
    private final BlacklistQueryService blacklistQueryService;

    public UUID createCaregiverBlacklist(CreateCaregiverBlacklistRequest request) {
        // 연관된 엔티티들 조회
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        Consumer consumer = consumerQueryService.getConsumer(request.consumerId());

        // 엔티티 생성
        Blacklist blacklist = blacklistMapper.toEntity(request, caregiver, consumer);
        blacklist.initializeBlacklistId(UUID.randomUUID());

        // 저장
        Blacklist savedBlacklist = blacklistRepository.save(blacklist);
        return savedBlacklist.getBlacklistId();
    }

    public void deleteCaregiverBlacklist(UUID caregiverBlacklistId){
        Blacklist blacklist = blacklistQueryService.getCaregiverBlacklist(caregiverBlacklistId);
        blacklistRepository.delete(blacklist);

    }
}