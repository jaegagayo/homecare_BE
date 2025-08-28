package jaega.homecare.domain.match.processor;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.settlement.repository.SettlementQueryRepository;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CaregiverFilterProcessor {

    private final SettlementQueryRepository settlementQueryRepository;

    public List<Caregiver> filter(ServiceRequest request, List<Caregiver> candidates, LocalDate applyDate) {
        return candidates.stream()
                .filter(c -> !hasOverlappingWork(c, applyDate, request.getPreferredStartTime(), request.getPreferredEndTime()))
                .collect(Collectors.toList());
    }


    private boolean hasOverlappingWork(Caregiver caregiver, LocalDate date, LocalTime start, LocalTime end) {
        return !settlementQueryRepository.findOverlappingWorkLog(caregiver, date, start, end).isEmpty();
    }
}
