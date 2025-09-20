package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.repository.CenterRepository;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyCenterService {

    private final CenterRepository centerRepository;
    private final UserRepository userRepository;

    public void generateDummyCenter(){
        createDummyCenter(0);
    }

    private void createDummyCenter(int index) {
        User user = userRepository.findByUserRole(UserRole.ROLE_CENTER).get(index);
        Center center = new Center();
        center.setCenter(UUID.randomUUID(), user, "순천시재가노인지원센터" + index, "전라남도 순천시 매곡동 1213 ", "061-749-1114" + String.format("%04d", index));
        centerRepository.save(center);
    }
}
