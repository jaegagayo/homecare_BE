package jaega.homecare.domain.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private String name;

    private String email;

    private String password;

    private String phone;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private LocalDateTime createdAt;

    @Builder
    public User(String name, String email, String password, String phone, LocalDate birthDate,
                UUID userId, UserRole userRole, LocalDateTime createdAt){
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    public void setUser(UUID userId, UserRole userRole, LocalDateTime createdAt){
        this.userId = userId;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }
}
