package jaega.homecare.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.users.dto.res.GetConsumerResponse;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "유저(공통) API")
@RequestMapping("/api/user")
public interface UserController {

    @Operation(summary = "유저 로그인 API", description = "입력된 유저의 아이디, 비밀번호를 통해 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "유저 로그인 성공")
    @ApiResponse(responseCode = "401", description = "로그인 실패 - 잘못된 인증 정보",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/login")
    ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request);

    @Operation(summary = "유저 로그아웃 API", description = "유저의 로그아웃을 진행합니다..")
    @ApiResponse(responseCode = "200", description = "유저 로그아웃 성공")
    @PostMapping("/logout")
    ResponseEntity<Void> logout();

    @Operation(summary = "유저 정보 조회 API", description = "유저 ID를 토대로 유저의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    @PostMapping("/{userId}")
    ResponseEntity<GetConsumerResponse> getConsumer(@PathVariable UUID userId);
}
