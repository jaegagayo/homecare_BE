package jaega.homecare.domain.match.service;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.match.dto.res.MatchingResponseDto;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.match.dto.req.CaregiverDTO;
import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.MatchResponse;
import jaega.homecare.domain.match.infra.AiRecommendation;
import jaega.homecare.domain.match.processor.CaregiverFilterProcessor;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaregiverMatchingService {

    private final ServiceRequestQueryService serviceRequestQueryService;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverFilterProcessor filterProcessor;
    private final AiRecommendation aiClient;

    public MatchResponse recommendCaregivers(UUID serviceRequestId) {
        ServiceRequest request = serviceRequestQueryService.getServiceRequest(serviceRequestId);
        List<Caregiver> all = caregiverRepository.findAllByStatus(CaregiverStatus.ACTIVE);

        Set<LocalDate> applyDates = request.getRequestedDays();

        // 날짜별 필터링 후 결과 합치기 (중복 제거)
        Set<Caregiver> filteredCaregivers = new HashSet<>();

        for (LocalDate date : applyDates) {
            List<Caregiver> candidates = filterProcessor.filter(request, all, date);
            filteredCaregivers.addAll(candidates);
        }

        List<Caregiver> finalCandidates = new ArrayList<>(filteredCaregivers);

        // 엔티티를 DTO로 변환
        List<CaregiverDTO> finalCandidateDTOs = convertCaregiversToDTOs(finalCandidates);

        ServiceRequestDTO dto = convertToDTO(request);

        MatchResponse response = aiClient.sendRecommendRequest(dto, finalCandidateDTOs);
        return enrichWithCaregiverNames(response);
    }

    private ServiceRequestDTO convertToDTO(ServiceRequest entity) {
        List<String> requestedDaysStr = entity.getRequestedDays().stream()
                .map(LocalDate::toString)  // "yyyy-MM-dd" 문자열로 변환
                .collect(Collectors.toList());

        return new ServiceRequestDTO(
                entity.getServiceRequestId().toString(),
                entity.getAddress(),
                List.of(entity.getLocation().getLatitude(), entity.getLocation().getLongitude()),
                entity.getServiceType().name(),
                requestedDaysStr
        );
    }


    // Caregiver 엔티티 → CaregiverDTO 변환
    private CaregiverDTO convertCaregiverToDTO(Caregiver caregiver) {
        String closedDays = caregiver.getDaysOff().stream()
                .map(day -> switch (day) {
                    case MONDAY -> "월";
                    case TUESDAY -> "화";
                    case WEDNESDAY -> "수";
                    case THURSDAY -> "목";
                    case FRIDAY -> "금";
                    case SATURDAY -> "토";
                    case SUNDAY -> "일";
                })
                .collect(Collectors.joining(","));

        String personalityType = Optional.ofNullable(caregiver.getPersonalityType())
                .orElse("UNKNOWN");  // 기본값 지정

        return new CaregiverDTO(
                caregiver.getCaregiverId().toString(),
                caregiver.getServiceTypes().stream().findFirst().orElse(ServiceType.VISITING_CARE).name(),
                closedDays,
                caregiver.getAvailableStartTime().toString(),
                caregiver.getAvailableEndTime().toString(),
                caregiver.getAddress(),
                List.of(
                        caregiver.getLocation().getLatitude(),
                        caregiver.getLocation().getLongitude()
                ),
                personalityType,
                5 // 요양보호사 경력 년차
        );
    }

    // 여러 Caregiver 엔티티 리스트를 DTO 리스트로 변환
    private List<CaregiverDTO> convertCaregiversToDTOs(List<Caregiver> caregivers) {
        return caregivers.stream()
                .map(this::convertCaregiverToDTO)
                .collect(Collectors.toList());
    }

    public MatchResponse enrichWithCaregiverNames(MatchResponse response) {
        Map<UUID, String> idToNameMap = caregiverRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Caregiver::getCaregiverId,
                        caregiver -> caregiver.getUser().getName()
                ));

        List<MatchingResponseDto> enrichedList = response.matchedCaregivers().stream()
                .map(dto -> new MatchingResponseDto(
                        dto.caregiverId(),
                        idToNameMap.getOrDefault(UUID.fromString(dto.caregiverId()), "이름없음"), 
                        dto.availableStartTime(),
                        dto.availableEndTime(),
                        dto.address(),
                        dto.location(),
                        dto.matchScore(),
                        dto.matchReason()
                ))
                .toList();

        return new MatchResponse(enrichedList, response.totalMatches());
    }
}
