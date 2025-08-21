package jaega.homecare.domain.Blacklist.entity;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "blacklist")
@NoArgsConstructor
public class Blacklist extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "blacklist_id", unique = true)
    private UUID blacklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Consumer consumer;

    @Builder
    public Blacklist(UUID blacklistId, Caregiver caregiver, Consumer consumer) {
        this.blacklistId = blacklistId;
        this.caregiver = caregiver;
        this.consumer = consumer;
    }

    public void initializeBlacklistId(UUID caregiverBlacklistId) {
        this.blacklistId = caregiverBlacklistId;
    }
}