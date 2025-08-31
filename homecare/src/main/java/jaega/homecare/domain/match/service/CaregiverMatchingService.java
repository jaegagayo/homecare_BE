package jaega.homecare.domain.match.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterQueryRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import jaega.homecare.domain.match.dto.req.CaregiverDTO;
import jaega.homecare.domain.match.dto.req.MatchRequest;
import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.MatchingResponseDTO;
import jaega.homecare.domain.match.infra.MatchingGrpcClient;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CaregiverMatchingService {

    private final ServiceRequestQueryService serviceRequestQueryService;
    private final CaregiverCenterQueryRepository caregiverCenterQueryRepository;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;
    private final MatchingGrpcClient matchingGrpcClient;

    public MatchingResponseDTO recommendCaregivers(UUID serviceRequestId) {
        ServiceRequest request = serviceRequestQueryService.getServiceRequest(serviceRequestId);
        List<Caregiver> caregiverList = caregiverCenterQueryRepository.findAllByStatus(CaregiverStatus.ACTIVE);

        List<CaregiverDTO> candidateCaregivers = convertCaregiverToDTO(caregiverList);
        ServiceRequestDTO serviceRequestDTO = convertServiceRequestToDto(request);

        MatchRequest matchRequest = new MatchRequest(
                serviceRequestDTO,
                candidateCaregivers
        );

        return matchingGrpcClient.getMatchingRecommendations(matchRequest);
    }

    private List<CaregiverDTO> convertCaregiverToDTO(List<Caregiver> caregivers) {
        if (caregivers == null || caregivers.isEmpty()) {
            return List.of();
        }

        return caregivers.stream()
                .map(caregiver -> {
                    // CaregiverPreference 조회
                    CaregiverPreference preference = caregiverPreferenceQueryService
                            .findCaregiverPreferenceByCaregiver(caregiver.getCaregiverId());

                    String addressType = (preference != null) ? preference.getAddressType().toString() : null;

                    // DTO 변환
                    List<Double> baseLocation = (preference != null && preference.getLocation() != null)
                            ? List.of(preference.getLocation().getLatitude(), preference.getLocation().getLongitude())
                            : List.of(0.0, 0.0);

                    String workArea = (preference != null) ? preference.getWorkArea() : null;

                    return new CaregiverDTO(
                            caregiver.getCaregiverId().toString(),
                            caregiver.getUser().getUserId().toString(),
                            caregiver.getUser().getName(),
                            workArea,
                            addressType,
                            baseLocation,
                            caregiver.getCareer().toString(),
                            caregiver.getKoreanProficiency().toString(),
                            caregiver.isAccompanyOuting(),
                            caregiver.getSelfIntroduction(),
                            caregiver.getVerifiedStatus().toString(),
                            preference
                    );
                })
                .toList();
    }

    private ServiceRequestDTO convertServiceRequestToDto(ServiceRequest serviceRequest){
        return new ServiceRequestDTO(
                serviceRequest.getServiceRequestId().toString(),
                serviceRequest.getConsumer().getConsumerId().toString(),
                serviceRequest.getServiceAddress(),
                serviceRequest.getAddressType().toString(),
                List.of(
                        serviceRequest.getLocation().getLatitude(),
                        serviceRequest.getLocation().getLongitude()
                ),
                serviceRequest.getRequestDate().toString(),
                serviceRequest.getPreferredStartTime().toString(),
                serviceRequest.getPreferredEndTime().toString(),
                serviceRequest.getDuration().toString(),
                serviceRequest.getServiceType().toString(),
                serviceRequest.getAdditionalInformation()
        );
    }
}
