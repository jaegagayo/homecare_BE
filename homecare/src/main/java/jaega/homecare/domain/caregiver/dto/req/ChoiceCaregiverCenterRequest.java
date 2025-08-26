package jaega.homecare.domain.caregiver.dto.req;

import java.util.UUID;

public record ChoiceCaregiverCenterRequest(
        UUID serviceMatchId,      // 보호사가 선택할 매칭 ID
        UUID caregiverCenterId,   // 보호사가 선택한 자신의 센터 ID
        Double distanceLog        // 거리 기록
) {}
