package jaega.homecare.domain.match.service;

import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.*;
import jaega.homecare.domain.review.repository.ReviewRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.users.entity.Disease;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CaregiverMatchingService {

    private final RestTemplate restTemplate;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceMatchRepository serviceMatchRepository;

    public MatchingResponse callRecommend(UUID serviceRequestId) {
        String url = "http://localhost:8000/matching/recommend";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ServiceRequest sr = serviceRequestQueryService.getServiceRequest(serviceRequestId);


        Map<String, Object> location = new HashMap<>();
        location.put("latitude", sr.getLocation().getLatitude());
        location.put("longitude", sr.getLocation().getLongitude());

        ServiceRequestDTO dto = new ServiceRequestDTO(
                sr.getServiceRequestId(),
                sr.getConsumer().getConsumerId(),
                sr.getServiceAddress(),
                sr.getAddressType().toString(),
                location,
                sr.getRequestDate().toString(),
                sr.getPreferredStartTime().toString(),
                sr.getPreferredEndTime().toString(),
                sr.getDuration(),
                sr.getServiceType().toString(),
                sr.getAdditionalInformation()
        );

        Map<String, Object> body = Map.of("serviceRequest", dto);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<MatchingResponse> response = restTemplate.postForEntity(url, request, MatchingResponse.class);

        MatchingResponse originalResponse = response.getBody();

        if (originalResponse == null) {
            return null; // 혹은 예외 처리
        }

        List<CaregiverInfo> updatedCaregivers =  originalResponse.matchedCaregivers().stream()
                .map(c -> {
                    var caregiver = caregiverRepository.findByCaregiverId(c.caregiverId())
                            .orElseThrow(() -> new RuntimeException("Caregiver not found: " + c.caregiverId()));

                    var preferences = caregiverPreferenceRepository.findByCaregiver_CaregiverId(c.caregiverId());

                    boolean dementia = preferences.stream().anyMatch(p -> p.getSupportedConditions().contains(Disease.DEMENTIA));
                    boolean bedridden = preferences.stream().anyMatch(p -> p.getSupportedConditions().contains(Disease.BEDRIDDEN));

                    int age = Period.between(caregiver.getUser().getBirthDate(), LocalDate.now()).getYears();

                    Double avgRating = reviewRepository.findAverageScoreByCaregiverId(c.caregiverId());

                    if (avgRating == 0.0) {
                        avgRating = 5.0;
                    }

                    Long totalMatches = serviceMatchRepository.countByCaregiverId(c.caregiverId());
                    Long cancelledMatches = serviceMatchRepository.countCancelledByCaregiverId(c.caregiverId());

                    double rejectionRate = (totalMatches == 0) ? 0.0 : (double) cancelledMatches / totalMatches * 100;

                    return new CaregiverInfo(
                            caregiver.getCaregiverId(),
                            caregiver.getUser().getName(),
                            caregiver.getUser().getGender().toString(),
                            age,
                            caregiver.getCareer(),
                            avgRating,
                            caregiver.getKoreanProficiency().toString(),
                            new SpecialCaseExperience(dementia, bedridden),
                            caregiver.isAccompanyOuting(),
                            rejectionRate,
                            caregiver.getSelfIntroduction()
                    );
                }).toList();

        return new MatchingResponse(
                originalResponse.serviceRequestId(),
                updatedCaregivers
        );

    }

}
