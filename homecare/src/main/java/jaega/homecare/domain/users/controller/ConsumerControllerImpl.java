package jaega.homecare.domain.users.controller;

import jaega.homecare.domain.users.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.ConsumerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;

    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerCreateRequest request) {
        consumerCommandService.createUser(request, UserRole.ROLE_CONSUMER);
        return ResponseEntity.noContent().build();
    }

}
