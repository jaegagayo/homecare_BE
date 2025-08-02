package jaega.homecare.domain.users.controller;

import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.dto.res.GetConsumerResponse;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.service.command.UserCommandService;
import jaega.homecare.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserControllerImpl implements UserController{

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Override
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userCommandService.userLogin(request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetConsumerResponse> getConsumer(@PathVariable UUID userId){
        GetConsumerResponse response = userQueryService.findConsumer(userId);
        return ResponseEntity.ok(response);
    }
}
