package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import jaega.homecare.domain.center.dto.req.SearchCaregiverResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverByCaregiverStatusResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverByServiceTypeResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverProfileResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverQueryService {

    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final CenterQueryService centerQueryService;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;
    private final CaregiverQueryRepository caregiverQueryRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverMapper caregiverMapper;

    // 도메인 조회용
    public Caregiver getCaregiver(UUID caregiverId){
        return caregiverRepository.findByCaregiverId(caregiverId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요양보호사입니다."));
    }

    // 요양보호사 관리자 인증 상태 조회
    /**
     *
     * Caregiver
     *
     */

    public GetCaregiverVerifiedStatusResponse getCaregiverVerifiedStatus(UUID caregiverId){
        Caregiver caregiver = getCaregiver(caregiverId);
        return caregiverMapper.toGetCaregiverVerifiedStatus(caregiver);
    }


    /**
     *
     * Center
     *
     */

    // 센터의 모든 요양보호사 조회
    public List<GetCaregiverResponse> getAllCaregiversByCenter(UUID centerId) {
        return caregiverQueryRepository.findAllByCenterId(centerId);
    }

    // 센터의 요양보호사 상태 기반 조회
    public List<GetCaregiverByCaregiverStatusResponse> getCaregiverByWorkStatus(UUID centerId, CaregiverStatus status){
        return caregiverQueryRepository.findCaregiverByCaregiverStatus(centerId, status);
    }

    // 센터의 요양보호사 서비스 타입별 조회
    public List<GetCaregiverByServiceTypeResponse> getCaregiverByServiceType(UUID centerId, Set<ServiceType> serviceTypes){
        return caregiverQueryRepository.findCaregiverByServiceTypes(centerId, serviceTypes);
    }

    // 요양보호사 프로필 정보 조회
    public GetCaregiverProfileResponse getCaregiverProfile(UUID caregiverId){
        Caregiver caregiver = getCaregiver(caregiverId);
        CaregiverPreference caregiverPreference = caregiverPreferenceQueryService.findCaregiverPreferenceByCaregiver(caregiverId);
        return caregiverMapper.toGetCaregiverProfile(caregiver, caregiverPreference);
    }

    // 센터 대시보드 통계 조회
    public GetDashboardPopularResponse getCaregiverStats(UUID centerId) {
        Center center = centerQueryService.findCenterByUUID(centerId);

        Long total = caregiverQueryRepository.countByCenterId(center.getCenterId());
        Long active = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.ACTIVE);
        Long inactive = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.INACTIVE);
        Long resigned = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.RESIGNED);
        Long newThisMonth = caregiverQueryRepository.countNewCaregiversThisMonth(center);

        return new GetDashboardPopularResponse(total, active, inactive, resigned, newThisMonth);
    }

    // 센터의 소속 등록을 위한 요양보호사 검색
    public List<SearchCaregiverResponse> searchCaregiver(String keyword){
        return caregiverQueryRepository.searchByNameOrPhone(keyword);
    }

}
