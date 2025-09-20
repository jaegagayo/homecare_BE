package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DummyRecurringOfferService {
    private final CaregiverRepository caregiverRepository;
    private final RecurringOfferRepository recurringOfferRepository;
    private final Random random = new Random();

    public UUID createDummyRecurringOfferForConsumer(Consumer consumer, boolean useFirstCaregiver) {
        List<Caregiver> approvedCaregivers = caregiverRepository.findAll();

        if (approvedCaregivers.isEmpty()) return null;

        Caregiver caregiver;
        if (useFirstCaregiver) {
            caregiver = approvedCaregivers.get(0); // ✅ 첫 번째 Caregiver 확정
        } else {
            caregiver = approvedCaregivers.get(random.nextInt(approvedCaregivers.size())); // 랜덤
        }

        // 근무 요일 랜덤
        Set<DayOfWeek> dayOfWeek = new HashSet<>();
        int numDays = 1 + random.nextInt(5);
        while (dayOfWeek.size() < numDays) {
            dayOfWeek.add(DayOfWeek.values()[random.nextInt(7)]);
        }

        LocalTime startTime = LocalTime.of(9 + random.nextInt(8), 0);
        LocalTime endTime = startTime.plusHours(3 + random.nextInt(3));

        LocalDate startDate = LocalDate.now().plusDays(random.nextInt(30));
        LocalDate endDate = startDate.plusWeeks(4 + random.nextInt(8));

        ServiceType serviceType = ServiceType.values()[random.nextInt(ServiceType.values().length)];

        RecurringOffer offer = RecurringOffer.builder()
                .recurringOfferId(UUID.randomUUID())
                .consumer(consumer)
                .caregiver(caregiver)
                .serviceAddress(consumer.getResidentialAddress())
                .addressType(AddressType.ROAD)
                .location(new Location(37.500 + random.nextDouble() * 0.1, 126.970 + random.nextDouble() * 0.1))
                .dayOfWeek(dayOfWeek)
                .serviceStartDate(startDate)
                .serviceEndDate(endDate)
                .serviceStartTime(startTime)
                .serviceEndTime(endTime)
                .serviceType(serviceType)
                .recurringStatus(RecurringStatus.PENDING)
                .recurringOfferUnread(true)
                .build();

        recurringOfferRepository.save(offer);
        return offer.getRecurringOfferId();
    }
}
