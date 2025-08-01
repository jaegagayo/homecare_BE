package jaega.homecare.domain.serviceMatch.controller;

import jaega.homecare.domain.serviceMatch.dto.res.ServiceMatchNotificationResponse;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumerSchedule")
public class ServiceMatchControllerImpl implements ServiceMatchController{

    private final ServiceMatchQueryService serviceMatchQueryService;


}
