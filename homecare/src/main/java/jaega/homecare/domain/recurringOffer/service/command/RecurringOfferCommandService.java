package jaega.homecare.domain.recurringOffer.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;
import jaega.homecare.domain.recurringOffer.mapper.RecurringOfferMapper;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jaega.homecare.domain.recurringOffer.service.query.RecurringOfferQueryService;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.command.ServiceRequestCommandService;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecurringOfferCommandService {

    private final RecurringOfferRepository recurringOfferRepository;
    private final RecurringOfferMapper recurringOfferMapper;
    private final RecurringOfferQueryService recurringOfferQueryService;
    private final CaregiverQueryService caregiverQueryService;
    private final ConsumerQueryService consumerQueryService;

    private final ServiceRequestQueryService serviceRequestQueryService;
    private final ServiceRequestCommandService serviceRequestCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;

    /**
     * 수요자 ( consumer )
     */

    public void createRecurringOffer(CreateRecurringOfferRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        Consumer consumer = consumerQueryService.getConsumer(request.consumerId());

        RecurringOffer recurringOffer = recurringOfferMapper.toEntity(request, caregiver, consumer);
        recurringOffer.initializeRecurringOffer(UUID.randomUUID());
        recurringOfferRepository.save(recurringOffer);

    }

    /**
     * 요양보호사 ( caregiver)
     */

    public void approveRecurringStatus(UUID recurringOfferId) {
        // 1️⃣ RecurringOffer 조회 및 상태 변경
        RecurringOffer recurringOffer = recurringOfferQueryService.getRecurringOffer(recurringOfferId);
        recurringOffer.changeRecurringStatus(RecurringStatus.APPROVED);

        // 2️⃣ ServiceRequest 생성
        int duration = (int) java.time.Duration.between(
                recurringOffer.getServiceStartTime(),
                recurringOffer.getServiceEndTime()
        ).toMinutes();

        // RecurringOffer -> ConsumerServiceRequest 매핑 (Mapper 사용)
        ConsumerServiceRequest serviceRequestDto = recurringOfferMapper.toConsumerServiceRequest(recurringOffer, duration);

        // 실제 ServiceRequest 생성 및 저장
        GetCreateServiceResponse createServiceResponse = serviceRequestCommandService.createServiceRequest(serviceRequestDto);

        // 생성된 ServiceRequest 조회
        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(createServiceResponse.serviceRequestId());

        // 3️⃣ ServiceMatch 반복 생성 (요일별)
        LocalDate currentDate = recurringOffer.getServiceStartDate();
        while (!currentDate.isAfter(recurringOffer.getServiceEndDate())) {
            if (recurringOffer.getDayOfWeek().contains(currentDate.getDayOfWeek())) {
                CreateServiceMatchRequest request = new CreateServiceMatchRequest(
                        serviceRequest.getServiceRequestId(),
                        recurringOffer.getCaregiver().getCaregiverId(),
                        recurringOffer.getServiceStartTime(),
                        recurringOffer.getServiceEndTime(),
                        currentDate
                );
                serviceMatchCommandService.createServiceMatch(request);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    public void rejectRecurringStatus(UUID recurringStatusId){
        RecurringOffer recurringOffer = recurringOfferQueryService.getRecurringOffer(recurringStatusId);
        recurringOffer.changeRecurringStatus(RecurringStatus.REJECTED);
    }
}
