package jaega.homecare.domain.serviceMatch.entity;

public enum MatchStatus {
    PENDING,        // 대기 중
    CANCELLED,      // 매칭 취소 (실패)
    CONFIRMED,      // 매칭 확정 (스케줄 예정)
    COMPLETED       // 일정 완료
}