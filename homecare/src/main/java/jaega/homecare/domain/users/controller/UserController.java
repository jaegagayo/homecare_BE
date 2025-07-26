package jaega.homecare.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User", description = "User API")
@RequestMapping("/api/user")
public interface UserController {

    @Operation(summary = "유저 기본 회원가입 API", description = "입력받은 정보로 유저의 기본 회원가입을 진행합니다.")
    @ApiResponse(responseCode = "204", description = "유저 생성 성공")
    @PostMapping("/register")
    ResponseEntity<Void> createConsumer(UserCreateRequest request);

}
