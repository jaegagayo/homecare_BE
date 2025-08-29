package jaega.homecare.domain.review.mapper;

import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.CaregiverReviewDetailResponse;
import jaega.homecare.domain.review.dto.res.CaregiverReviewItem;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


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

    @Mapping(target = "serviceDate", source = "serviceMatch.serviceDate")
    @Mapping(target = "caregiverName", source = "serviceMatch.caregiver.user.name")
    ConsumerReviewResponse toConsumerReviewResponse(Review review);

    List<ConsumerReviewResponse> toConsumerReviewResponse(List<Review> reviews);

    // TODO: ServiceMatch에서 일정 요약 가져오기
    @Mapping(target = "scheduleSummary", ignore = true)
    CaregiverReviewItem toCaregiverReviewItem(Review review);

    // TODO: ServiceMatch에서 일정 상세 가져오기
    @Mapping(target = "scheduleDetail", ignore = true)
    CaregiverReviewDetailResponse toDetailResponse(Review review);
}