package jaega.homecare.domain.review.entity;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "review_id", unique = true)
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_match_id")
    private ServiceMatch serviceMatch;

    @Column(name = "review_score", nullable = false)
    private Double reviewScore; // 리뷰 점수 (1-5점)

    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent; // 리뷰 내용

    @Builder
    public Review(UUID reviewId, ServiceMatch serviceMatch, Double reviewScore,
                  String reviewContent) {
        this.reviewId = reviewId;
        this.reviewContent = reviewContent;
        this.serviceMatch = serviceMatch;
        this.reviewScore = reviewScore;
        this.reviewContent = reviewContent;
    }

    public void initializeReview(UUID reviewId) {
        this.reviewId = reviewId;
    }
}