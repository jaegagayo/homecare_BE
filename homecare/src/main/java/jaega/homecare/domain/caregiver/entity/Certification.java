package jaega.homecare.domain.caregiver.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "certification")
@NoArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "certification_id", unique = true)
    private UUID certificationID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    private String CertificationNumber;

    private LocalDate CertificationDate;

    private boolean trainStatus;

    @Builder
    public Certification(UUID certificationId, Caregiver caregiver, String certificationNumber, LocalDate certificationDate, boolean trainStatus){
        this.certificationID = certificationId;
        this.caregiver = caregiver;
        this.CertificationNumber = certificationNumber;
        this.CertificationDate = certificationDate;
        this.trainStatus = false;
    }

    public void setCertification(UUID certificationID){
        this.certificationID = certificationID;
    }

    public void changeStatus(boolean trainStatus){
        this.trainStatus = !trainStatus;
    }
}
