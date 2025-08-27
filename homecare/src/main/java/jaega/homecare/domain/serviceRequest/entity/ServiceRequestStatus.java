package jaega.homecare.domain.serviceRequest.entity;

public enum ServiceRequestStatus {
    PENDING,     // 대기
    ASSIGNED,    // 배정 완료
    CANCELED,    // 취소
    RECURRING,   // 정기 제안
}
