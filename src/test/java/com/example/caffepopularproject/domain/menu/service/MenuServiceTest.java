package com.example.caffepopularproject.domain.menu.service;

import com.example.caffepopularproject.common.exception.ErrorCode;
import com.example.caffepopularproject.common.exception.ServiceException;
import com.example.caffepopularproject.domain.menu.dto.request.MenuCreateRequest;
import com.example.caffepopularproject.domain.menu.entity.Menu;
import com.example.caffepopularproject.domain.menu.repository.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 생성 실패 - 가격이 음수일 경우 예외 발생")
    void createMenu_Fail_NegativePrice() {

        // given
        MenuCreateRequest request = new MenuCreateRequest(
                "아메리카노",
                -2000L,
                10L
        );

        // when&then
        assertThatThrownBy(() -> menuService.createMenu(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_MENU_VALUE.getMessage());

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 생성 실패 - 재고가 음수일 경우 예외 발생")
    void createMenu_Fail_NegativeStock() {

        // given
        MenuCreateRequest request = new MenuCreateRequest(
                "아메리카노",
                2000L,
                -3L
        );

        // when&then
        assertThatThrownBy(() -> menuService.createMenu(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.INVALID_MENU_VALUE.getMessage());

        verify(menuRepository, never()).save(any(Menu.class));
    }
}
