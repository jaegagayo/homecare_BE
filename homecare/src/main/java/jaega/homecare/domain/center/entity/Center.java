package jaega.homecare.domain.center.entity;

import jaega.homecare.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "center")
@NoArgsConstructor
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "center_id", unique = true)
    private UUID centerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String center_address;

    private String contact_number;

    public void setCenter(UUID centerId, User user, String name, String center_address, String contact_number){
        this.centerId = centerId;
        this.user = user;
        this.name = name;
        this.center_address = center_address;
        this.contact_number = contact_number;
    }
}
