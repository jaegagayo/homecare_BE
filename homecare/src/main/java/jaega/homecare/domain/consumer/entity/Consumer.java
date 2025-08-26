package jaega.homecare.domain.consumer.entity;

import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "consumer")
@NoArgsConstructor
public class Consumer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "consumer_id", unique = true)
    private UUID consumerId;

    @Column(name = "residential_address", nullable = false)
    private String residentialAddress;

    @Column(name = "visit_address", nullable = false)
    private String visitAddress;

    @Column(name = "entranceType", columnDefinition = "TEXT")
    private String entranceType;

    @Column(name = "care_grade", nullable = false)
    private Integer careGrade;

    @Column(name = "is_medical_aid", nullable = false)
    private boolean isMedicalAid;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Enumerated(EnumType.STRING)
    private Disease disease;

    @Enumerated(EnumType.STRING)
    private CognitiveStatus cognitiveStatus;

    @Column(name ="livingSituation", columnDefinition = "TEXT")
    private String livingSituation;

    @Column(name = "guardian_name", nullable = false)
    private String guardianName;                            // 보호자 이름

    @Column(name = "guardian_phone", nullable = false)
    private String guardianPhone;                           // 보호자 연락처

    @Builder
    public Consumer(User user, UUID consumerId, String residentialAddress, String visitAddress, String entranceType,
                    Integer careGrade, boolean isMedicalAid, Integer weight, Disease disease, CognitiveStatus cognitiveStatus,
                    String livingSituation, String guardianName, String guardianPhone){
        this.user = user;
        this.consumerId = consumerId;
        this.residentialAddress = residentialAddress;
        this.visitAddress = visitAddress;
        this.entranceType = entranceType;
        this.careGrade = careGrade;
        this.isMedicalAid = isMedicalAid;
        this.weight = weight;
        this.disease = disease;
        this.cognitiveStatus = cognitiveStatus;
        this.livingSituation = livingSituation;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
    }

    public void initializeConsumer(UUID consumerId){
        this.consumerId = consumerId;
    }

}