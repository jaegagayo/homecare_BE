package jaega.homecare.domain.center.dto.req;

public record CenterLoginRequest (
        String email,
        String password
){
}
