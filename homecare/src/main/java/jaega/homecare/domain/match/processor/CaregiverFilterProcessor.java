package jaega.homecare.domain.match.processor;

import jaega.homecare.domain.workMatch.repository.WorkMatchQueryRepository;
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
                .filter(c -> isAvailableAtTime(c, request.getPreferredStartTime(), request.getPreferredEndTime()))
                .filter(c -> supportsServiceType(c, request.getServiceType()))
                .filter(c -> !hasOverlappingWork(c, applyDate, request.getPreferredStartTime(), request.getPreferredEndTime()))
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
        return !workMatchQueryRepository.findOverlappingWorkMatch(caregiver, date, start, end).isEmpty();
    }
}
