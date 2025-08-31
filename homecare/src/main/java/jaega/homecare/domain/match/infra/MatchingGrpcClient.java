package jaega.homecare.domain.match.infra;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jaega.homecare.domain.match.dto.req.MatchRequest;
import jaega.homecare.domain.match.dto.res.MatchingResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class MatchingGrpcClient {

    /*
    private final MatchingServiceGrpc.MatchingServiceBlockingStub stub;

    public MatchingGrpcClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        this.stub = MatchingServiceGrpc.newBlockingStub(channel);
    }

     */

    public MatchingResponseDTO getMatchingRecommendations(MatchRequest request) {
        return null;
     //   return stub.getMatchingRecommendations(request);
    }

    /*
    public HealthCheckResponse healthCheck() {
        HealthCheckRequest request = HealthCheckRequest.newBuilder()
                .setService("matching")
                .build();
        return stub.healthCheck(request);
    }


     */


}