package jaega.homecare.domain.users.controller;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserControllerImpl implements UserController{

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody UserCreateRequest request) {
        userCommandService.createUser(request, UserRole.ROLE_CONSUMER);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userCommandService.userLogin(request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
