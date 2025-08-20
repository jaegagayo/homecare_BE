package jaega.homecare.domain.consumer.entity;

import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
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

    @Column(name = "consumer_id", unique = true)
    private UUID consumerId;

    @Column(name = "residential_address", nullable = false)
    private String residentialAddress;

    @Column(name = "visit_addres", nullable = false)
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

    @Column(name ="livingSituation", columnDefinition = "TEXT")
    private String livingSituation;

    @Column(name = "guardian_name", nullable = false)
    private String guardianName;

    @Column(name = "guardian_phone", nullable = false)
    private String guardianPhone;

}