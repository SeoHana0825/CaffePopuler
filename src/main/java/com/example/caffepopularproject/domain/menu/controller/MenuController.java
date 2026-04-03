package com.example.caffepopularproject.domain.menu.controller;

import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.dto.response.MenuRankingResponse;
import com.example.caffepopularproject.domain.menu.dto.response.MenuResponse;
import com.example.caffepopularproject.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(
            @Valid @RequestBody MenuCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(menuService.createMenu(request));

    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenu(
    ) {
        return ResponseEntity.ok(menuService.getAllMenu());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> getMenu(
            @PathVariable Long menuId
    ) {
        return ResponseEntity.ok(menuService.getMenu(menuId));
    }

    // 인기 메뉴 조회 - Redis에 날짜별로 구분된 카테고리 값들을 조회 (최근 7일 기준 기준으로 조회)
    @GetMapping("/popular")
    public ResponseEntity<List<MenuRankingResponse>> getPopularMenu(
            @RequestParam(required = false) LocalDate standardDate
    ) {
        LocalDate date = (standardDate != null) ? standardDate : LocalDate.now();

        List<MenuRankingResponse> response = menuService.findTop3MenuInLast7Days(date);

        return ResponseEntity.ok(response);
    }
}
