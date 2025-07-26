package jaega.homecare.domain.users.controller;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserControllerImpl implements UserController{

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Override
    public ResponseEntity<Void> createConsumer(UserCreateRequest request) {
        userCommandService.createConsumer(request);
        return ResponseEntity.noContent().build();
    }
}
