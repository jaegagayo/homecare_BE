package jaega.homecare.domain.users.entity;

import jaega.homecare.domain.users.dto.req.UserUpdateRequest;
import jaega.homecare.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Builder
    public User(String name, String email, String password, String phone, LocalDate birthDate,
                UUID userId, UserRole userRole, Gender gender){
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public void setUser(UUID userId, UserRole userRole, LocalDateTime createdAt){
        this.userId = userId;
        this.userRole = userRole;
    }

    public void updateUser(UserUpdateRequest request){
        this.name = request.name();
        this.birthDate = request.birthDate();
        this.gender = request.gender();
        this.phone = request.phone();
    }
}
