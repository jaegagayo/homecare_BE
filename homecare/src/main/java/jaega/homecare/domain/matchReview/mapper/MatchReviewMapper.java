package jaega.homecare.domain.matchReview.mapper;

import jaega.homecare.domain.matchReview.dto.req.CreateMatchReviewRequest;
import jaega.homecare.domain.matchReview.dto.res.GetMatchReviewResponse;
import jaega.homecare.domain.matchReview.entity.MatchReview;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MatchReviewMapper {

    @Mapping(target = "serviceMatch", source = "serviceMatch")
    @Mapping(target = "writer", source = "writer")
    @Mapping(target = "caregiverName", source = "request.caregiverName")
    @Mapping(target = "serviceMatchTime", source = "request.serviceMatchTime")
    @Mapping(target = "reviewScore", source = "request.reviewScore")
    @Mapping(target = "reviewContent", source = "request.reviewContent")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "matchReviewId", ignore = true)
    MatchReview toEntity(CreateMatchReviewRequest request, ServiceMatch serviceMatch, User writer, LocalDateTime createdAt, LocalDateTime updatedAt);

    @Mapping(target = "matchReviewId", source = "matchReviewId")
    @Mapping(target = "serviceMatchId", source = "serviceMatch.serviceMatchId")
    @Mapping(target = "writerName", source = "writer.name")
    @Mapping(target = "caregiverName", source = "caregiverName")
    @Mapping(target = "serviceMatchTime", source = "serviceMatchTime")
    @Mapping(target = "reviewScore", source = "reviewScore")
    @Mapping(target = "reviewContent", source = "reviewContent")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    GetMatchReviewResponse toGetResponse(MatchReview matchReview);
}