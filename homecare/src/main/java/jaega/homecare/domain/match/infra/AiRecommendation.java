package jaega.homecare.domain.match.infra;

import jaega.homecare.domain.match.dto.req.CaregiverDTO;
import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiRecommendation {

    private final WebClient webClient;

    public MatchResponse sendRecommendRequest(ServiceRequestDTO request, List<CaregiverDTO> caregivers) {
        Map<String, Object> requestBody = Map.of(
                "serviceRequest", request,
                "candidateCaregivers", caregivers
        );

        return webClient.post()
                .uri("/matching/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            // 에러 로그 출력
                            System.err.println("에러 메세지 : " + errorBody);
                            return Mono.error(new RuntimeException("API 호출 실패: " + errorBody));
                        }))
                .bodyToMono(MatchResponse.class)
                .block();
    }
}
