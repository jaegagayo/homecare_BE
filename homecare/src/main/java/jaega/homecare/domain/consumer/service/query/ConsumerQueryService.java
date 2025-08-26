package jaega.homecare.domain.consumer.service.query;

import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerNextScheduleResponse;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.repository.ConsumerQueryRepository;
import jaega.homecare.domain.consumer.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumerQueryService {

    private final ConsumerRepository consumerRepository;
    private final ConsumerQueryRepository consumerQueryRepository;

    public Consumer getConsumer(UUID consumerId){
        return consumerRepository.findByConsumerId(consumerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 고객입니다."));
    }

    public List<ConsumerScheduleResponse> getConsumerSchedule(UUID consumerId, LocalDate today){
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return consumerQueryRepository.findWeeklySchedule(consumerId, weekStart, weekEnd);
    }

    public ConsumerScheduleDetailResponse getScheduleDetail(UUID serviceRequestId){
        return consumerQueryRepository.findScheduleDetail(serviceRequestId);
    }

    public ConsumerNextScheduleResponse getNextSchedule(UUID consumerId){
        return consumerQueryRepository.findNextSchedule(consumerId);
    }
}
