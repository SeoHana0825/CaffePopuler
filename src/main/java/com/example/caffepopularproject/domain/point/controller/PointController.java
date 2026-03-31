package com.example.caffepopularproject.domain.point.controller;

import com.example.caffepopularproject.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/points")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge/{userId}")
    public ResponseEntity<String> chargePoint (
            @PathVariable Long userId,
            @RequestParam Long amount
    ) {
        pointService.chargePoint(userId, amount);
        return ResponseEntity.ok("포인트 충전이 완료되었습니다.");
    }
}
