package jaega.homecare.domain.matchReview.mapper;

import jaega.homecare.domain.matchReview.dto.req.CreateReviewRequest;
import jaega.homecare.domain.matchReview.dto.res.GetReviewResponse;
import jaega.homecare.domain.matchReview.entity.Review;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "serviceMatch", source = "serviceMatch")
    @Mapping(target = "reviewScore", source = "request.reviewScore")
    @Mapping(target = "reviewContent", source = "request.reviewContent")
    @Mapping(target = "reviewId", ignore = true)
    Review toEntity(CreateReviewRequest request, ServiceMatch serviceMatch);

    @Mapping(target = "reviewId", source = "reviewId")
    @Mapping(target = "serviceMatchId", source = "serviceMatch.serviceMatchId")
    @Mapping(target = "reviewScore", source = "reviewScore")
    @Mapping(target = "reviewContent", source = "reviewContent")
    GetReviewResponse toGetResponse(Review review);
}