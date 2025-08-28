package jaega.homecare.domain.consumer.service.query;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumerQueryService {

    private final ConsumerRepository consumerRepository;

    public Consumer getConsumer(UUID consumerId){
        return consumerRepository.findByConsumerId(consumerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 고객입니다."));
    }
}
