package jaega.homecare.domain.serviceMatch.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.mapper.ServiceMatchMapper;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.service.query.VoucherQueryService;
import jaega.homecare.domain.voucherUsage.service.command.VoucherUsageCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceMatchCommandService {

    private final ServiceMatchRepository serviceMatchRepository;
    private final VoucherQueryService voucherQueryService;
    private final VoucherUsageCommandService voucherUsageCommandService;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final CaregiverQueryService caregiverQueryService;
    private final ServiceMatchMapper serviceMatchMapper;

    public UUID createServiceMatch(CreateServiceMatchRequest request){
        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(request.serviceRequestId());

        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());

        ServiceMatch serviceMatch = serviceMatchMapper.toEntity(request, serviceRequest, caregiver);
        serviceMatch.initializeServiceMatch(UUID.randomUUID());
        serviceMatchRepository.save(serviceMatch);

        UUID voucherId = voucherQueryService.getVoucherIdByConsumerId(serviceRequest.getConsumer().getConsumerId());
        Voucher voucher = voucherQueryService.getVoucher(voucherId);

        voucherUsageCommandService.createVoucherUsage(voucher, serviceMatch);

        return serviceMatch.getServiceMatchId();
    }
}
