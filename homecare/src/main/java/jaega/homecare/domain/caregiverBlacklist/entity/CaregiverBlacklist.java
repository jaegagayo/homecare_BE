package jaega.homecare.domain.caregiverBlacklist.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "caregiver_blacklist")
@NoArgsConstructor
public class CaregiverBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "blacklist_id", unique = true)
    private UUID blacklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason; // 신고 이유

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public CaregiverBlacklist(UUID blacklistId, Caregiver caregiver, User reporter, 
                            String reason, LocalDateTime createdAt) {
        this.blacklistId = blacklistId;
        this.caregiver = caregiver;
        this.reporter = reporter;  
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public void setBlacklistId(UUID blacklistId) {
        this.blacklistId = blacklistId;
    }
}