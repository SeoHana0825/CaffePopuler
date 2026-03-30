package com.example.caffepopularproject.domain.menu.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.dto.response.MenuResponse;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    /* 메뉴 API
    * 1. 메뉴 생성
    * 2. 메뉴 단건 조회
    * 3. 메뉴 목록 조회
    * 4. 인기 메뉴 조회
    * 5. 메뉴 수정
    * 6. 메뉴 상태 변경
    * */

    @Transactional
    public MenuResponse createMenu (MenuCreateRequest request) {

        if (request.getPrice() < 0 || request.getStock() < 0) {
            throw new ServiceException(ErrorCode.INVALID_MENU_VALUE);
        }
        Menu menu = Menu.register(
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        Menu saveMenu = menuRepository.save(menu);

        return MenuResponse.from(saveMenu);
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenu (Long id) {

        Menu menu = menuRepository.findById (id)
                .orElseThrow(() -> new ServiceException(ErrorCode.MENU_NOT_FOUND));

        return MenuResponse.from(menu);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenu () {
        List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getPopularMenus() {

        List<Menu> popularMenus = menuRepository.findTop3ByOrderCountDesc();

        return popularMenus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

}
