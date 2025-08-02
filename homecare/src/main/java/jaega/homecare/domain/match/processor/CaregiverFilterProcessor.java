package jaega.homecare.domain.match.processor;

import jaega.homecare.domain.WorkMatch.repository.WorkMatchQueryRepository;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CaregiverFilterProcessor {

    private final WorkMatchQueryRepository workMatchQueryRepository;

    public List<Caregiver> filter(ServiceRequest request, List<Caregiver> candidates, LocalDate applyDate) {
        return candidates.stream()
                .filter(c -> !isDayOff(c, applyDate.getDayOfWeek()))
                .filter(c -> isAvailableAtTime(c, request.getPreferred_time_start(), request.getPreferred_time_end()))
                .filter(c -> supportsServiceType(c, request.getServiceType()))
                .filter(c -> !hasOverlappingWork(c, applyDate, request.getPreferred_time_start(), request.getPreferred_time_end()))
                .collect(Collectors.toList());
    }

    private boolean isDayOff(Caregiver caregiver, DayOfWeek day) {
        return caregiver.getDaysOff() != null && caregiver.getDaysOff().contains(day);
    }

    private boolean isAvailableAtTime(Caregiver caregiver, LocalTime start, LocalTime end) {
        return !caregiver.getAvailableStartTime().isAfter(start) &&
                !caregiver.getAvailableEndTime().isBefore(end);
    }

    private boolean supportsServiceType(Caregiver caregiver, ServiceType type) {
        return caregiver.getServiceTypes().contains(type);
    }

    private boolean hasOverlappingWork(Caregiver caregiver, LocalDate date, LocalTime start, LocalTime end) {
        return !workMatchQueryRepository.findOverlappingWorkMatches(caregiver, date, start, end).isEmpty();
    }
}
