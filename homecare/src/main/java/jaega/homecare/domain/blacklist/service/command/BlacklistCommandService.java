package jaega.homecare.domain.blacklist.service.command;

import jaega.homecare.domain.blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.blacklist.entity.Blacklist;
import jaega.homecare.domain.blacklist.mapper.BlacklistMapper;
import jaega.homecare.domain.blacklist.repository.BlacklistRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.blacklist.service.query.BlacklistQueryService;
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

    public UUID createBlacklistByConsumer(CreateBlacklistByConsumerRequest request) {
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

    public void deleteBlacklistByConsumer(UUID caregiverBlacklistId){
        Blacklist blacklist = blacklistQueryService.getBlacklist(caregiverBlacklistId);
        blacklistRepository.delete(blacklist);

    }
}