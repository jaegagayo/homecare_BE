package jaega.homecare.domain.matchReview.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetMatchReviewResponse(
        UUID matchReviewId,
        UUID serviceMatchId,
        String writerName,
        String caregiverName,
        Integer serviceMatchTime,
        Integer reviewScore,
        String reviewContent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}