package jaega.homecare.domain.blacklist.service.query;

import jaega.homecare.domain.blacklist.dto.res.GetBlacklistByConsumerResponse;
import jaega.homecare.domain.blacklist.entity.Blacklist;
import jaega.homecare.domain.blacklist.mapper.BlacklistMapper;
import jaega.homecare.domain.blacklist.repository.BlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlacklistQueryService {

    private final BlacklistRepository blacklistRepository;
    private final BlacklistMapper blacklistMapper;

    public Blacklist getBlacklist(UUID blacklistId){
        return blacklistRepository.findByBlacklistId(blacklistId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 블랙리스트입니다."));
    }

    // 특정 신고자의 블랙리스트 조회
    public List<GetBlacklistByConsumerResponse> getBlacklistByConsumer(UUID consumerId) {
        List<Blacklist> blacklists = blacklistRepository.findByConsumer_ConsumerId(consumerId);
        return blacklists.stream()
                .map(blacklistMapper::toGetResponse)
                .toList();
    }
}