package jaega.homecare.domain.serviceMatch.entity;

public enum MatchStatus {
    PENDING,    // 대기 중
    CONFIRMED,  // 매칭 완료
    RETRY,      // 매칭 재시도
    CANCELLED   // 매칭 취소 (실패)
}