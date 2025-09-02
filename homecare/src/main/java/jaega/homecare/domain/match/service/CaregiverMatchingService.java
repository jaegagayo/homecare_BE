package jaega.homecare.domain.match.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterQueryRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import jaega.homecare.domain.match.dto.req.CaregiverDTO;
import jaega.homecare.domain.match.dto.req.MatchRequest;
import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.MatchedCaregiverDTO;
import jaega.homecare.domain.match.dto.res.MatchingResponseDTO;
import jaega.homecare.domain.match.infra.MatchingGrpcClient;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaregiverMatchingService {
    /*

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

        MatchingServiceOuterClass.MatchingRequest grpcRequest =
                toProto(serviceRequestDTO, candidateCaregivers);


        MatchingServiceOuterClass.MatchingResponse response = matchingGrpcClient.getMatchingRecommendations(grpcRequest);
        return toDto(response);
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

    public static MatchingServiceOuterClass.MatchingRequest toProto(
            ServiceRequestDTO serviceRequestDTO,
            List<CaregiverDTO> caregivers
    ) {
        // ServiceRequest 변환
        MatchingServiceOuterClass.ServiceRequest serviceRequest =
                MatchingServiceOuterClass.ServiceRequest.newBuilder()
                        .setServiceRequestId(serviceRequestDTO.serviceRequestId())
                        .setConsumerId(serviceRequestDTO.consumerId())
                        .setServiceAddress(serviceRequestDTO.serviceAddress())
                        .setAddressType(serviceRequestDTO.addressType() != null ? serviceRequestDTO.addressType() : "")
                        .setPreferredStartTime(serviceRequestDTO.preferredStartTime())
                        .setPreferredEndTime(serviceRequestDTO.preferredEndTime())
                        .setDuration(serviceRequestDTO.duration())
                        .setServiceType(serviceRequestDTO.serviceType())
                        .setRequestStatus(ServiceRequestStatus.ASSIGNED.toString())
                        .setRequestDate(serviceRequestDTO.requestDate())
                        .setAdditionalInformation(serviceRequestDTO.additionalInformation() != null ? serviceRequestDTO.additionalInformation() : "")
                        .setLocation(MatchingServiceOuterClass.Location.newBuilder()
                                .setLatitude(serviceRequestDTO.location().get(0))
                                .setLongitude(serviceRequestDTO.location().get(1))
                                .build())
                        .build();

        // CaregiverDTO → CaregiverForMatching 변환
        List<MatchingServiceOuterClass.CaregiverForMatching> protoCaregivers = caregivers.stream()
                .map(c -> MatchingServiceOuterClass.CaregiverForMatching.newBuilder()
                        .setCaregiverId(c.caregiverId())
                        .setUserId(c.userId())
                        .setName(c.name())
                        .setAddress(c.address() != null ? c.address() : "")
                        .setAddressType(c.addressType() != null ? c.addressType() : "")
                        .setServiceType(c.preferences().getServiceTypes() != null ? c.preferences().getServiceTypes().toString() : "")
                        .setCareer(c.career() != null ? c.career() : "")
                        .setKoreanProficiency(c.koreanProficiency() != null ? c.koreanProficiency() : "")
                        .setIsAccompanyOuting(c.isAccompanyOuting())
                        .setSelfIntroduction(c.selfIntroduction() != null ? c.selfIntroduction() : "")
                        .setVerifiedStatus(c.verifiedStatus() != null ? c.verifiedStatus() : "")
                        .build())
                .toList();

        return MatchingServiceOuterClass.MatchingRequest.newBuilder()
                .setServiceRequest(serviceRequest)
                .addAllCandidateCaregivers(protoCaregivers)
                .build();
    }

    public static MatchingResponseDTO toDto(MatchingServiceOuterClass.MatchingResponse response) {
        if (response == null) {
            return null;
        }

        List<MatchedCaregiverDTO> matchedCaregivers = response.getMatchedCaregiversList().stream()
                .map(c -> new MatchedCaregiverDTO(
                        c.getCaregiverId(),
                        c.getName(),
                        c.getDistanceKm(),
                        c.getEstimatedTravelTime(),
                        c.getMatchScore(),
                        c.getAddress(),
                        c.getAddressType(),
                        List.of(c.getLocation().getLatitude(), c.getLocation().getLongitude())
                                .stream().map(Object::toString).collect(Collectors.toList()),
                        c.getCareer(),
                        c.getSelfIntroduction()
                ))
                .collect(Collectors.toList());

        return new MatchingResponseDTO(
                "", // serviceRequestId는 필요하면 별도로 매핑
                matchedCaregivers,
                matchedCaregivers.size(),
                response.getTotalMatches()
        );
    }

     */
}
