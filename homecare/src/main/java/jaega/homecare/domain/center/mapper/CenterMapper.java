package jaega.homecare.domain.center.mapper;

import jaega.homecare.domain.center.dto.res.CenterLoginResponse;
import jaega.homecare.domain.center.entity.Center;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CenterMapper {

    CenterLoginResponse toLoginResponse(Center center);
}
