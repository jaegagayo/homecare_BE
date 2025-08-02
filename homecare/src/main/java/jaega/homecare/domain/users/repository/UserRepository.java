package jaega.homecare.domain.users.repository;

import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByUserId(UUID userId);

    User findByEmail(String email);

    List<User> findByUserRole(UserRole role);
}
