package jaega.homecare.domain.consumer.entity;

import jaega.homecare.domain.consumer.dto.req.ConsumerUpdateRequest;
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
    private String residentialAddress;          // 거주지 주소

    @Column(name = "visit_address", nullable = false)
    private String visitAddress;                // 방문지 주소

    @Column(name = "entranceType", columnDefinition = "TEXT")
    private String entranceType;                // 출입 방법

    @Column(name = "care_grade", nullable = false)
    private Integer careGrade;                  // 장기요양 인정 등급

    @Column(name = "is_medical_aid", nullable = false)
    private boolean isMedicalAid;               // 의료급여 수급자 여부

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Enumerated(EnumType.STRING)
    private Disease disease;                    // 질병 여부

    @Enumerated(EnumType.STRING)
    private CognitiveStatus cognitiveStatus;    // 인지 상태

    @Column(name ="livingSituation", columnDefinition = "TEXT")
    private String livingSituation;             // 동거 여부

    @Column(name = "guardian_name", nullable = false)
    private String guardianName;                // 보호자 이름

    @Column(name = "guardian_phone", nullable = false)
    private String guardianPhone;               // 보호자 연락처

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

    public void updateConsumer(ConsumerUpdateRequest request){
        this.residentialAddress = request.residentialAddress();
        this.visitAddress = request.visitAddress();
        this.entranceType = request.entranceType();
        this.careGrade = request.careGrade();
        this.isMedicalAid = request.medicalAid();
        this.weight = request.weight();
        this.disease = request.disease();
        this.cognitiveStatus = request.cognitiveStatus();
        this.livingSituation = request.livingSituation();
        this.guardianName = request.guardianName();
        this.guardianPhone = request.guardianPhone();
    }

}