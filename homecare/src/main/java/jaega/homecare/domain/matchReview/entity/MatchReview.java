package jaega.homecare.domain.matchReview.entity;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "match_review")
@NoArgsConstructor
public class MatchReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "match_review_id", unique = true)
    private UUID matchReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_match_id")
    private ServiceMatch serviceMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(name = "caregiver_name", nullable = false)
    private String caregiverName;

    @Column(name = "service_match_time", nullable = false)
    private Integer serviceMatchTime; // 서비스 진행 시간 (분 단위)

    @Column(name = "review_score", nullable = false)
    private Integer reviewScore; // 리뷰 점수 (1-5점)

    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent; // 리뷰 내용

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public MatchReview(UUID matchReviewId, ServiceMatch serviceMatch, User writer, 
                      String caregiverName, Integer serviceMatchTime, Integer reviewScore, 
                      String reviewContent, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.matchReviewId = matchReviewId;
        this.serviceMatch = serviceMatch;
        this.writer = writer;
        this.caregiverName = caregiverName;
        this.serviceMatchTime = serviceMatchTime;
        this.reviewScore = reviewScore;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setMatchReviewId(UUID matchReviewId) {
        this.matchReviewId = matchReviewId;
    }
}