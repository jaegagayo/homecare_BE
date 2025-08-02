package jaega.homecare.global.dummy.controller;

import jaega.homecare.global.dummy.service.DummyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dummy")
@RequiredArgsConstructor
public class DummyDataController {

    private final DummyDataService dummyDataService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateDummyData() {
        dummyDataService.generateAllDummyData();
        return ResponseEntity.ok("더미 데이터 생성이 완료되었습니다.");
    }
}
