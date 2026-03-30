package com.example.caffepopularproject.domain.menu.controller;

import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.dto.response.MenuResponse;
import com.example.caffepopularproject.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
